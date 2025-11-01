package cn.nukkit.entity.pathfinding;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
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
    private final Map<String, Boolean> moveCache = new ConcurrentHashMap<>();
    private final Map<PathRequest, Future<List<Vector3>>> pendingRequests = new ConcurrentHashMap<>();

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

        int maxIterations = 200;
        int iterations = 0;

        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;
            PathNode currentNode = openSet.poll();
            closedSet.add(currentNode);

            if (currentNode.equals(endNode) || getDistance(currentNode, endNode) <= 2) {
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
        for (PathNode neighbor : getNeighbors(currentNode)) {
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
                if (cosAngle < 0.95) {
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

            if (isValidMove(node, nx, ny, nz)) {
                neighbors.add(new PathNode(nx, ny, nz));
            }
        }

        return neighbors;
    }

    private boolean isValidMove(PathNode from, int x, int y, int z) {
        String cacheKey = from.x + ":" + from.y + ":" + from.z + ":" + x + ":" + y + ":" + z;
        return moveCache.computeIfAbsent(cacheKey, k -> calculateValidMove(from, x, y, z));
    }

    private boolean calculateValidMove(PathNode from, int x, int y, int z) {
        Block block = level.getBlock(x, y, z);
        if (block.getId() != Block.AIR && block.getId() != Block.COBWEB && !block.canPassThrough()) {
            return false;
        }

        Block belowBlock = level.getBlock(x, y - 1, z);
        boolean isBelowSolid = belowBlock.isSolid() ||
                belowBlock instanceof BlockSlab ||
                belowBlock instanceof BlockStairs;

        if (!isBelowSolid) return false;

        int deltaY = y - from.y;
        if (deltaY > 2 || deltaY < -3) return false;

        Block headBlock = level.getBlock(x, y + 1, z);
        if (!headBlock.canPassThrough() && !(headBlock instanceof BlockSlab)) {
            return false;
        }

        if (from.x != x && from.z != z) {
            Block corner1 = level.getBlock(from.x, y, z);
            Block corner2 = level.getBlock(x, y, from.z);
            if ((!corner1.canPassThrough() && !(corner1 instanceof BlockSlab)) ||
                    (!corner2.canPassThrough() && !(corner2 instanceof BlockSlab))) {
                return false;
            }
        }

        return true;
    }

    private double getDistance(PathNode a, PathNode b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
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

    private static class PathRequest {
        private final Vector3 start;
        private final Vector3 target;

        public PathRequest(Vector3 start, Vector3 target) {
            this.start = start;
            this.target = target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathRequest that = (PathRequest) o;
            return Objects.equals(start, that.start) && Objects.equals(target, that.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, target);
        }
    }
}