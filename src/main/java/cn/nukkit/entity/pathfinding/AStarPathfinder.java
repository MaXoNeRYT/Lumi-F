package cn.nukkit.entity.pathfinding;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;

import java.util.*;
import java.util.concurrent.*;

public class AStarPathfinder {

    private static final int[][] DIRECTIONS = {
            {-1, 0, 0}, {1, 0, 0}, {0, 0, -1}, {0, 0, 1},
            {-1, 0, -1}, {-1, 0, 1}, {1, 0, -1}, {1, 0, 1},
            {0, 1, 0}, {0, 2, 0}, {0, -1, 0}, {0, -2, 0},
            {-1, 1, 0}, {1, 1, 0}, {0, 1, -1}, {0, 1, 1},
            {-1, -1, 0}, {1, -1, 0}, {0, -1, -1}, {0, -1, 1}
    };

    private static final ExecutorService SHARED_EXECUTOR = Executors.newFixedThreadPool(2);
    private static final Map<Level, AStarPathfinder> INSTANCES = new ConcurrentHashMap<>();
    private static volatile boolean shutdown = false;

    private final Level level;
    private final Map<Long, Boolean> moveCache = new ConcurrentHashMap<>();
    private final Map<PathRequest, Future<List<Vector3>>> pendingRequests = new ConcurrentHashMap<>();

    private static final int MAX_CACHE_SIZE = 5000;
    private int cacheCleanupCounter = 0;
    private static final int CACHE_CLEANUP_INTERVAL = 200;

    private static final double ENTITY_WIDTH = 0.6;
    private static final double ENTITY_HEIGHT = 1.8;

    private AStarPathfinder(Level level) {
        this.level = level;
    }

    public static AStarPathfinder getInstance(Level level) {
        return INSTANCES.computeIfAbsent(level, AStarPathfinder::new);
    }

    public Future<List<Vector3>> findPathAsync(Vector3 start, Vector3 target) {
        if (shutdown) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        PathRequest request = new PathRequest(start, target);

        synchronized (pendingRequests) {
            Future<List<Vector3>> existingFuture = pendingRequests.get(request);
            if (existingFuture != null && !existingFuture.isDone()) {
                return existingFuture;
            }

            Future<List<Vector3>> future = SHARED_EXECUTOR.submit(() -> findPathSync(start, target));
            pendingRequests.put(request, future);
            return future;
        }
    }

    public List<Vector3> findPathSync(Vector3 start, Vector3 target) {
        if (shutdown) {
            return new ArrayList<>();
        }

        PathNode startNode = new PathNode(start.getFloorX(), start.getFloorY(), start.getFloorZ());
        PathNode endNode = new PathNode(target.getFloorX(), target.getFloorY(), target.getFloorZ());

        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(PathNode::getFCost));
        Set<PathNode> closedSet = new HashSet<>();

        startNode.gCost = 0;
        startNode.hCost = getDistance(startNode, endNode);
        openSet.add(startNode);

        int maxIterations = 150;
        int iterations = 0;

        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;

            PathNode currentNode = openSet.poll();
            closedSet.add(currentNode);

            if (currentNode.equals(endNode) || getDistance(currentNode, endNode) <= 0.2) {
                return reconstructPath(currentNode);
            }

            exploreNeighbors(currentNode, endNode, openSet, closedSet);
        }

        return findClosestPath(closedSet, endNode);
    }

    public List<Vector3> findPath(Vector3 start, Vector3 target) {
        return findPathSync(start, target);
    }

    private void exploreNeighbors(PathNode currentNode, PathNode endNode,
                                  PriorityQueue<PathNode> openSet, Set<PathNode> closedSet) {
        List<PathNode> neighbors = getNeighbors(currentNode);

        for (PathNode neighbor : neighbors) {
            if (closedSet.contains(neighbor)) continue;

            double newGCost = currentNode.gCost + getDistance(currentNode, neighbor);

            if (newGCost < neighbor.gCost || !openSet.contains(neighbor)) {
                neighbor.gCost = newGCost;
                neighbor.hCost = getDistance(neighbor, endNode);
                neighbor.parent = currentNode;

                openSet.remove(neighbor);
                openSet.add(neighbor);
            }
        }
    }

    private List<Vector3> findClosestPath(Set<PathNode> closedSet, PathNode endNode) {
        PathNode closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (PathNode node : closedSet) {
            double dist = getDistance(node, endNode);
            if (dist < closestDistance) {
                closestDistance = dist;
                closest = node;
            }
        }

        if (closest != null && closestDistance < 10) {
            return reconstructPath(closest);
        }

        return new ArrayList<>();
    }

    private List<Vector3> reconstructPath(PathNode endNode) {
        List<Vector3> path = new ArrayList<>();
        PathNode current = endNode;

        while (current != null) {
            path.add(new Vector3(current.x + 0.5, current.y, current.z + 0.5));
            current = current.parent;
        }

        Collections.reverse(path);
        return simplifyPath(path);
    }

    private List<Vector3> simplifyPath(List<Vector3> path) {
        if (path.size() <= 2) return path;

        List<Vector3> simplified = new ArrayList<>();
        simplified.add(path.get(0));

        for (int i = 1; i < path.size() - 1; i++) {
            Vector3 prev = simplified.get(simplified.size() - 1);
            Vector3 next = path.get(i + 1);

            double dx1 = path.get(i).x - prev.x;
            double dz1 = path.get(i).z - prev.z;
            double dx2 = next.x - path.get(i).x;
            double dz2 = next.z - path.get(i).z;

            double dot = dx1 * dx2 + dz1 * dz2;
            double mag1 = Math.sqrt(dx1 * dx1 + dz1 * dz1);
            double mag2 = Math.sqrt(dx2 * dx2 + dz2 * dz2);

            if (mag1 > 0 && mag2 > 0) {
                double cosAngle = dot / (mag1 * mag2);
                if (cosAngle < 0.9) {
                    simplified.add(path.get(i));
                }
            }
        }

        simplified.add(path.get(path.size() - 1));
        return simplified;
    }

    private List<PathNode> getNeighbors(PathNode node) {
        List<PathNode> neighbors = new ArrayList<>();

        for (int[] dir : DIRECTIONS) {
            int nx = node.x + dir[0];
            int ny = node.y + dir[1];
            int nz = node.z + dir[2];

            Block targetBlock = level.getBlock(nx, ny, nz);
            AxisAlignedBB targetBB = targetBlock.getBoundingBox();
            if (targetBB != null) {
                double targetHeight = targetBB.getMaxY() - targetBB.getMinY();
                if (targetHeight > 1.0) {
                    continue;
                }
            }

            if (isValidMove(node, nx, ny, nz)) {
                neighbors.add(new PathNode(nx, ny, nz));
            }
        }

        return neighbors;
    }

    private boolean isValidMove(PathNode from, int x, int y, int z) {
        long cacheKey = packPosition(from.x, from.y, from.z, x, y, z);

        Boolean cached = moveCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        boolean result = calculateValidMove(from, x, y, z);

        if (moveCache.size() < MAX_CACHE_SIZE) {
            moveCache.put(cacheKey, result);
        } else {
            cleanupCacheIfNeeded();
            moveCache.put(cacheKey, result);
        }

        return result;
    }

    private boolean calculateValidMove(PathNode from, int x, int y, int z) {
        Block block = level.getBlock(x, y, z);

        AxisAlignedBB blockBB = block.getBoundingBox();
        if (blockBB != null) {
            double blockHeight = blockBB.getMaxY() - blockBB.getMinY();
            if (blockHeight > 1.0) {
                return false;
            }
        }

        if (!canEntityPassThroughBlock(block, x, y, z)) {
            return false;
        }

        Block belowBlock = level.getBlock(x, y - 1, z);
        if (!isBlockWalkable(belowBlock)) {
            return false;
        }

        int deltaY = y - from.y;
        if (deltaY > 2 || deltaY < -3) return false;

        Block headBlock = level.getBlock(x, y + 1, z);
        if (!canEntityPassThroughBlock(headBlock, x, y + 1, z)) {
            return false;
        }

        if (from.x != x && from.z != z) {
            Block corner1 = level.getBlock(from.x, y, z);
            Block corner2 = level.getBlock(x, y, from.z);

            AxisAlignedBB corner1BB = corner1.getBoundingBox();
            if (corner1BB != null) {
                double corner1Height = corner1BB.getMaxY() - corner1BB.getMinY();
                if (corner1Height > 1.0) return false;
            }

            AxisAlignedBB corner2BB = corner2.getBoundingBox();
            if (corner2BB != null) {
                double corner2Height = corner2BB.getMaxY() - corner2BB.getMinY();
                if (corner2Height > 1.0) return false;
            }

            if (!canEntityPassThroughBlock(corner1, from.x, y, z) ||
                    !canEntityPassThroughBlock(corner2, x, y, from.z)) {
                return false;
            }
        }

        return true;
    }

    private boolean canEntityPassThroughBlock(Block block, int x, int y, int z) {
        if (block.getId() == Block.AIR) {
            return true;
        }

        AxisAlignedBB blockBB = block.getBoundingBox();

        if (blockBB != null) {
            double blockHeight = blockBB.getMaxY() - blockBB.getMinY();

            if (blockHeight > 1.0) {
                return false;
            }
        }

        if (block.canPassThrough()) {
            return true;
        }

        if (blockBB == null) {
            return true;
        }

        double blockHeight = blockBB.getMaxY() - blockBB.getMinY();

        double entityMinX = x + 0.5 - ENTITY_WIDTH / 2;
        double entityMaxX = x + 0.5 + ENTITY_WIDTH / 2;
        double entityMinY = y;
        double entityMaxY = y + ENTITY_HEIGHT;
        double entityMinZ = z + 0.5 - ENTITY_WIDTH / 2;
        double entityMaxZ = z + 0.5 + ENTITY_WIDTH / 2;

        AxisAlignedBB entityBB = new SimpleAxisAlignedBB(
                entityMinX, entityMinY, entityMinZ,
                entityMaxX, entityMaxY, entityMaxZ
        );

        boolean intersects = blockBB.intersectsWith(entityBB);

        if (!intersects) {
            return true;
        }

        if (blockHeight < 0.5) {
            return true;
        }

        double blockWidthX = blockBB.getMaxX() - blockBB.getMinX();
        double blockWidthZ = blockBB.getMaxZ() - blockBB.getMinZ();

        if (blockWidthX < 0.8 && blockWidthZ < 0.8 && blockHeight <= 1.0) {
            return canWalkAround(blockBB, entityBB);
        }

        return false;
    }


    private boolean canWalkAround(AxisAlignedBB blockBB, AxisAlignedBB entityBB) {
        double blockCenterX = (blockBB.getMinX() + blockBB.getMaxX()) / 2;
        double blockCenterZ = (blockBB.getMinZ() + blockBB.getMaxZ()) / 2;

        double entityCenterX = (entityBB.getMinX() + entityBB.getMaxX()) / 2;
        double entityCenterZ = (entityBB.getMinZ() + entityBB.getMaxZ()) / 2;

        double centerDistX = Math.abs(blockCenterX - entityCenterX);
        double centerDistZ = Math.abs(blockCenterZ - entityCenterZ);

        return centerDistX > 0.2 || centerDistZ > 0.2;
    }

    private boolean isBlockWalkable(Block block) {
        AxisAlignedBB bb = block.getBoundingBox();

        if (bb == null) {
            return false;
        }

        double blockHeight = bb.getMaxY() - bb.getMinY();

        if (block.isSolid() && blockHeight >= 0.5) {
            return true;
        }

        if (blockHeight > 0 && blockHeight < 1.0) {
            return true;
        }

        return false;
    }

    private double getDistance(PathNode a, PathNode b) {
        int dx = a.x - b.x;
        int dy = a.y - b.y;
        int dz = a.z - b.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private void cleanupCacheIfNeeded() {
        cacheCleanupCounter++;
        if (cacheCleanupCounter >= CACHE_CLEANUP_INTERVAL) {
            moveCache.clear();
            synchronized (pendingRequests) {
                pendingRequests.entrySet().removeIf(entry -> entry.getValue().isDone());
            }
            cacheCleanupCounter = 0;
        }
    }

    public void clearCache() {
        moveCache.clear();
        synchronized (pendingRequests) {
            pendingRequests.entrySet().removeIf(entry -> entry.getValue().isDone());
        }
    }

    public static void shutdown() {
        shutdown = true;
        SHARED_EXECUTOR.shutdown();
        try {
            if (!SHARED_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                SHARED_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            SHARED_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }

        for (AStarPathfinder instance : INSTANCES.values()) {
            instance.clearCache();
        }
        INSTANCES.clear();
    }

    public static void onLevelUnload(Level level) {
        AStarPathfinder instance = INSTANCES.remove(level);
        if (instance != null) {
            instance.clearCache();
        }
    }

    private static long packPosition(int fx, int fy, int fz, int tx, int ty, int tz) {
        long hash = (long) fx * 73856093L ^ (long) fy * 19349663L ^ (long) fz * 83492791L;
        hash ^= (long) tx * 50331653L ^ (long) ty * 25165843L ^ (long) tz * 12582917L;
        return hash;
    }

    private static class PathRequest {
        private final Vector3 start;
        private final Vector3 target;
        private final int hashCode;

        public PathRequest(Vector3 start, Vector3 target) {
            this.start = start;
            this.target = target;
            this.hashCode = Objects.hash(
                    start.getFloorX(), start.getFloorY(), start.getFloorZ(),
                    target.getFloorX(), target.getFloorY(), target.getFloorZ()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathRequest that = (PathRequest) o;

            return start.getFloorX() == that.start.getFloorX() &&
                    start.getFloorY() == that.start.getFloorY() &&
                    start.getFloorZ() == that.start.getFloorZ() &&
                    target.getFloorX() == that.target.getFloorX() &&
                    target.getFloorY() == that.target.getFloorY() &&
                    target.getFloorZ() == that.target.getFloorZ();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}