package cn.nukkit.entity;

import cn.nukkit.entity.pathfinding.AStarPathfinder;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public abstract class PathfindingMob extends BaseEntity {
    protected final AStarPathfinder pathfinder;
    protected List<Vector3> currentPath = new ArrayList<>();
    protected int pathIndex = 0;
    protected int repathCooldown = 0;
    protected int stuckTicks = 0;
    protected Vector3 lastPosition = null;
    protected Vector3 lastTarget = null;
    protected Future<List<Vector3>> pendingPathFuture = null;
    protected int pathfindingWaitTicks = 0;
    protected boolean isWaitingForPath = false;

    public PathfindingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.pathfinder = AStarPathfinder.getInstance(this.level);
    }

    protected boolean findPathToTarget(Vector3 target) {
        if (repathCooldown > 0 && !currentPath.isEmpty()) return true;

        if (isWaitingForPath && pendingPathFuture != null) {
            if (pendingPathFuture.isDone()) {
                try {
                    List<Vector3> newPath = pendingPathFuture.get();
                    if (!newPath.isEmpty()) {
                        this.currentPath = newPath;
                        this.pathIndex = 0;
                        this.stuckTicks = 0;
                        this.lastPosition = this.getPosition();
                        this.lastTarget = target;
                        this.repathCooldown = 15;
                    }
                    this.pendingPathFuture = null;
                    this.isWaitingForPath = false;
                    this.pathfindingWaitTicks = 0;
                    return !newPath.isEmpty();
                } catch (Exception e) {
                    this.pendingPathFuture = null;
                    this.isWaitingForPath = false;
                    this.pathfindingWaitTicks = 0;
                    return false;
                }
            } else {
                pathfindingWaitTicks++;
                if (pathfindingWaitTicks > 40) {
                    pendingPathFuture.cancel(true);
                    pendingPathFuture = null;
                    isWaitingForPath = false;
                    pathfindingWaitTicks = 0;
                }
                return false;
            }
        }

        if (shouldRecalculatePath(target)) {
            this.pendingPathFuture = pathfinder.findPathAsync(this.getPosition(), target);
            this.isWaitingForPath = true;
            this.pathfindingWaitTicks = 0;
            return false;
        }

        return !currentPath.isEmpty();
    }

    protected boolean isStuck() {
        if (this.lastPosition == null) {
            this.lastPosition = this.getPosition();
            return false;
        }

        double distanceMoved = this.distance(lastPosition);
        if (distanceMoved < 0.1) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }

        lastPosition = this.getPosition();
        return stuckTicks > 20;
    }

    protected void followPath() {
        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) return;

        Vector3 nextPoint = currentPath.get(pathIndex);
        double x = nextPoint.x - this.x;
        double z = nextPoint.z - this.z;
        double diff = Math.sqrt(x * x + z * z);

        if (diff > 0.1) {
            this.motionX = this.getSpeed() * moveMultiplier * 0.1 * (x / diff);
            this.motionZ = this.getSpeed() * moveMultiplier * 0.1 * (z / diff);
            updateYaw(x, z);
        }

        if (this.distance(nextPoint) < 0.8) {
            pathIndex++;
        }
    }

    protected void updateYaw(double x, double z) {
        double yaw = Math.toDegrees(-Math.atan2(x, z));
        this.setBothYaw(yaw);
    }

    protected void moveDirectlyTo(Vector3 target) {
        double x = target.x - this.x;
        double z = target.z - this.z;
        double diff = Math.sqrt(x * x + z * z);

        if (diff > 0.1) {
            this.motionX = this.getSpeed() * moveMultiplier * 0.1 * (x / diff);
            this.motionZ = this.getSpeed() * moveMultiplier * 0.1 * (z / diff);
            updateYaw(x, z);
        } else {
            this.motionX = 0;
            this.motionZ = 0;
        }
    }

    protected boolean shouldRecalculatePath(Vector3 target) {
        return currentPath.isEmpty() ||
                repathCooldown <= 0 ||
                !target.equals(lastTarget) ||
                isStuck() ||
                (target instanceof Entity && this.distance(target) < 2);
    }

    public void clearPath() {
        currentPath.clear();
        pathIndex = 0;
        stuckTicks = 0;
        if (pendingPathFuture != null && !pendingPathFuture.isDone()) {
            pendingPathFuture.cancel(true);
        }
        pendingPathFuture = null;
        isWaitingForPath = false;
        pathfindingWaitTicks = 0;
    }

    @Override
    public void close() {
        super.close();
        clearPath();
    }
}