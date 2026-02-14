package cn.nukkit.entity;

import cn.nukkit.block.*;
import cn.nukkit.entity.passive.EntityIronGolem;
import cn.nukkit.entity.passive.EntitySkeletonHorse;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityWalking extends PathfindingMob {

    private boolean isCloseToTarget  = false;
    private int     lookAtTargetTicks = 0;

    private Vector3 lastMovementPosition = null;
    private int     notMovingTicks       = 0;
    private static final int STUCK_THRESHOLD = 15;

    private static final Map<Long, Boolean> reachableCache  = new ConcurrentHashMap<>();
    private static final Map<Long, Boolean> visibilityCache = new ConcurrentHashMap<>();
    private static int cacheCleanupCounter = 0;
    private static final int CACHE_CLEANUP_INTERVAL = 100;
    private static final int CACHE_MAX_SIZE         = 1000;

    private int aiUpdateInterval    = 0;
    private int targetCheckInterval = 0;
    private static final int AI_UPDATE_EVERY    = 2;
    private static final int TARGET_CHECK_EVERY = 5;

    private Boolean lastCanSeeResult    = null;
    private Vector3 lastSeenCheckTarget = null;
    private int     lastSeenCheckAge    = 0;
    private int     idleWanderCooldown  = 0;
    private static final int IDLE_WANDER_INTERVAL = 80;

    public EntityWalking(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private AxisAlignedBB getBlockBB(int x, int y, int z) {
        return level.getBlock(x, y, z).getBoundingBox();
    }

    protected void checkTarget() {
        if (this.isKnockback()) return;

        if (this.followTarget != null && !this.followTarget.closed &&
                this.followTarget.isAlive() && this.followTarget.canBeFollowed()) {
            return;
        }

        Vector3 target = this.target;

        if (target instanceof EntityCreature) {
            EntityCreature creatureTarget = (EntityCreature) target;
            if (!creatureTarget.closed && targetOption(creatureTarget, distanceSquared(target))
                    && ((Entity) target).canBeFollowed()) {
                return;
            }
        }

        double   near     = Integer.MAX_VALUE;
        Entity[] entities = this.getLevel().getEntities();

        for (Entity entity : entities) {
            if (entity == this || !(entity instanceof EntityCreature) ||
                    entity.closed || !this.canTarget(entity)) continue;

            EntityCreature creature = (EntityCreature) entity;
            if (creature instanceof BaseEntity) {
                BaseEntity base = (BaseEntity) creature;
                if (base.isFriendly() == this.isFriendly() && !this.isInLove()) continue;
            }

            double distance = this.distanceSquared(creature);
            if (distance > near || !this.targetOption(creature, distance)) continue;

            near           = distance;
            this.stayTime  = 0;
            this.moveTime  = 0;
            this.target    = creature;
        }

        if (this.target instanceof EntityCreature) {
            EntityCreature t = (EntityCreature) this.target;
            if (!t.closed && t.isAlive() && targetOption(t, distanceSquared(this.target))) return;
        }

        if (this.stayTime > 0) {
            if (Utils.rand(1, 100) > 5) return;
            findRandomTarget(10, 5, 15);
        } else if (Utils.rand(1, 100) == 1) {
            this.stayTime = Utils.rand(100, 200);
            findRandomTarget(10, 5, 15);
        } else if (this.moveTime <= 0 || this.target == null) {
            this.stayTime = 0;
            this.moveTime = Utils.rand(100, 200);
            findRandomTarget(15, 10, 20);
        }
    }

    private void findRandomTarget(int attempts, int minDist, int maxDist) {
        for (int i = 0; i < attempts; i++) {
            int x = Utils.rand(minDist, maxDist);
            int z = Utils.rand(minDist, maxDist);
            int fX = Utils.rand() ? x : -x;
            double fY = Utils.rand(-10.0, 10.0) / 10;
            int fZ = Utils.rand() ? z : -z;

            Vector3 candidate = this.add(fX, fY, fZ);
            if (canReachPosition(candidate) && !isBlockedForPath(candidate)) {
                this.target = candidate;
                break;
            }
        }
    }

    private boolean isBlockedForPath(Vector3 pos) {
        int x = pos.getFloorX(), y = pos.getFloorY(), z = pos.getFloorZ();

        Block block = level.getBlock(x, y, z);
        Block below = level.getBlock(x, y - 1, z);

        if (block instanceof BlockLiquid || block instanceof BlockLava) return true;
        if (below instanceof BlockLiquid || below instanceof BlockLava) return true;
        if (below instanceof BlockAir) return true;
        if (!isBlockWalkable(below)) return true;

        AxisAlignedBB blockBB = getBlockBB(x, y, z);
        if (blockBB != null) {
            double blockHeight = blockBB.getMaxY() - blockBB.getMinY();
            if (blockHeight > 1.0) return true;
        }

        if (!canEntityPassThrough(block, x, y, z)) return true;

        return false;
    }

    private void tryIdleWander() {
        if (idleWanderCooldown > 0) { idleWanderCooldown--; return; }
        if (this.followTarget != null || this.target instanceof Entity) return;
        if (!currentPath.isEmpty() || isWaitingForPath) return;

        for (int i = 0; i < 10; i++) {
            int dx = Utils.rand(-10, 10), dz = Utils.rand(-10, 10), dy = Utils.rand(-1, 1);
            Vector3 pos = this.add(dx, dy, dz);
            if (!canReachPosition(pos) || isBlockedForPath(pos)) continue;
            this.target = pos;
            findPathToTarget(pos);
            idleWanderCooldown = IDLE_WANDER_INTERVAL + Utils.rand(0, 40);
            return;
        }
        idleWanderCooldown = 40;
    }

    private boolean calculateReachable(int x, int y, int z) {
        if (isBlockedForPath(new Vector3(x, y, z))) return false;

        Block block = level.getBlock(x, y, z);
        Block below = level.getBlock(x, y - 1, z);
        Block above = level.getBlock(x, y + 1, z);

        if (block instanceof BlockLiquid || below instanceof BlockLiquid) return false;
        if (block instanceof BlockAir ) return false;

        if (isBlockPassableForMovement(block, x, y, z)
                && isBlockWalkable(below)
                && isBlockPassableForMovement(above, x, y + 1, z)) {
            return true;
        }

        Block above2 = level.getBlock(x, y + 2, z);
        return canEntityPassThrough(above,  x, y + 1, z)
                && canEntityPassThrough(above2, x, y + 2, z)
                && isBlockWalkable(block);
    }


    private boolean canEntityPassThrough(Block block, int x, int y, int z) {
        if (block.getId() == Block.AIR) return true;

        if (block.canPassThrough()) {
            AxisAlignedBB bb = getBlockBB(x, y, z);
            if (bb != null) {
                double height = bb.getMaxY() - bb.getMinY();
                if (height > 1.0) return false;
            }
            return true;
        }

        AxisAlignedBB blockBB = getBlockBB(x, y, z);
        if (blockBB == null) return true;

        double blockHeight = blockBB.getMaxY() - blockBB.getMinY();
        if (blockHeight > 1.0) {
            return false;
        }

        double width = getEntityWidth();
        double height = getEntityHeight();

        double entityMinX = x + 0.5 - width / 2;
        double entityMaxX = x + 0.5 + width / 2;
        double entityMinZ = z + 0.5 - width / 2;
        double entityMaxZ = z + 0.5 + width / 2;

        AxisAlignedBB entityBB = new SimpleAxisAlignedBB(
                entityMinX, y, entityMinZ,
                entityMaxX, y + height, entityMaxZ
        );

        if (!blockBB.intersectsWith(entityBB)) return true;

        if (blockHeight < 0.5) return true;

        double blockWidthX = blockBB.getMaxX() - blockBB.getMinX();
        double blockWidthZ = blockBB.getMaxZ() - blockBB.getMinZ();

        if (blockWidthX < 0.8 && blockWidthZ < 0.8) return canWalkAround(blockBB, entityBB);

        return false;
    }

    private boolean canWalkAround(AxisAlignedBB blockBB, AxisAlignedBB entityBB) {
        double centerDistX = Math.abs((blockBB.getMinX() + blockBB.getMaxX()) / 2
                - (entityBB.getMinX() + entityBB.getMaxX()) / 2);
        double centerDistZ = Math.abs((blockBB.getMinZ() + blockBB.getMaxZ()) / 2
                - (entityBB.getMinZ() + entityBB.getMaxZ()) / 2);
        return centerDistX > 0.2 || centerDistZ > 0.2;
    }

    private boolean isBlockWalkable(Block block) {
        AxisAlignedBB bb = getBlockBB(block.getFloorX(), block.getFloorY(), block.getFloorZ());
        if (bb == null) return false;

        double blockHeight = bb.getMaxY() - bb.getMinY();
        if (block.isSolid() && blockHeight >= 0.5) return true;
        if (blockHeight > 0 && blockHeight < 1.0)  return true;

        return false;
    }

    private boolean isBlockPassableForMovement(Block block, int x, int y, int z) {
        return canEntityPassThrough(block, x, y, z);
    }

    private boolean canReachPosition(Vector3 pos) {
        int x = NukkitMath.floorDouble(pos.x);
        int y = NukkitMath.floorDouble(pos.y);
        int z = NukkitMath.floorDouble(pos.z);

        long    key    = packPosition(x, y, z);
        Boolean cached = reachableCache.get(key);
        if (cached != null) return cached;

        boolean result = calculateReachable(x, y, z);

        if (reachableCache.size() < CACHE_MAX_SIZE) reachableCache.put(key, result);

        return result;
    }

    private boolean canSeeTarget(Vector3 target) {
        if (!(target instanceof Entity)) return false;

        if (lastSeenCheckTarget == target && lastSeenCheckAge < 10) {
            lastSeenCheckAge++;
            return lastCanSeeResult;
        }

        double distance = this.distance(target);
        if (distance > 16 || Math.abs(target.y - this.y) > 1.5) {
            lastCanSeeResult    = false;
            lastSeenCheckTarget = target;
            lastSeenCheckAge    = 0;
            return false;
        }

        double dx = target.x - this.x;
        double dy = (target.y + ((Entity) target).getHeight() * 0.5) - (this.y + this.getEyeHeight());
        double dz = target.z - this.z;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (len < 0.1) {
            lastCanSeeResult = true; lastSeenCheckTarget = target; lastSeenCheckAge = 0;
            return true;
        }

        boolean result = canSeePosition(
                this.x, this.y + this.getEyeHeight(), this.z,
                target.x, target.y + ((Entity) target).getHeight() * 0.5, target.z);

        lastCanSeeResult    = result;
        lastSeenCheckTarget = target;
        lastSeenCheckAge    = 0;
        return result;
    }

    private boolean canSeePosition(double sX, double sY, double sZ,
                                   double eX, double eY, double eZ) {
        double dx = eX - sX, dy = eY - sY, dz = eZ - sZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.1) return true;

        dx /= dist; dy /= dist; dz /= dist;
        int steps = (int) (dist / 0.5) + 1;

        for (int i = 1; i < steps; i++) {
            double cX = sX + dx * i * 0.5;
            double cY = sY + dy * i * 0.5;
            double cZ = sZ + dz * i * 0.5;

            int bx = NukkitMath.floorDouble(cX);
            int by = NukkitMath.floorDouble(cY);
            int bz = NukkitMath.floorDouble(cZ);

            if (!canRayPassThrough(bx, by, bz, cX, cY, cZ)) return false;
            if (!canRayPassThrough(bx, by + 1, bz, cX, cY + 1, cZ)) return false;

            if (i > 1) {
                double pX = sX + dx * (i - 1) * 0.5, pZ = sZ + dz * (i - 1) * 0.5;
                int pBx = NukkitMath.floorDouble(pX), pBz = NukkitMath.floorDouble(pZ);

                if (bx != pBx && bz != pBz) {
                    if (!canRayPassThrough(pBx, by, bz, pBx + 0.5, cY, bz + 0.5)) return false;
                    if (!canRayPassThrough(bx, by, pBz, bx + 0.5, cY, pBz + 0.5)) return false;
                    if (!canRayPassThrough(pBx, by + 1, bz, pBx + 0.5, cY + 1, bz + 0.5)) return false;
                    if (!canRayPassThrough(bx, by + 1, pBz, bx + 0.5, cY + 1, pBz + 0.5)) return false;
                }
            }
        }
        return true;
    }

    private boolean canRayPassThrough(int bx, int by, int bz,
                                      double rayX, double rayY, double rayZ) {
        Block block = level.getBlock(bx, by, bz);
        if (block.getId() == Block.AIR || block.canPassThrough()) return true;

        AxisAlignedBB blockBB = block.getBoundingBox();
        if (blockBB == null) return true;

        if (!blockBB.isVectorInside(new Vector3(rayX, rayY, rayZ))) return true;

        double blockHeight = blockBB.getMaxY() - blockBB.getMinY();
        return blockHeight < 0.5;
    }

    private boolean isStuckMovingToTarget() {
        if (this.target == null && this.followTarget == null) {
            notMovingTicks = 0; lastMovementPosition = null; return false;
        }
        if (!currentPath.isEmpty()) {
            notMovingTicks = 0; lastMovementPosition = this.getPosition(); return false;
        }
        if (lastMovementPosition == null) {
            lastMovementPosition = this.getPosition(); notMovingTicks = 0; return false;
        }

        if (this.distance(lastMovementPosition) > 0.1) {
            notMovingTicks = 0; lastMovementPosition = this.getPosition(); return false;
        }

        notMovingTicks++;
        if (notMovingTicks % 5 == 0) lastMovementPosition = this.getPosition();
        return notMovingTicks >= STUCK_THRESHOLD;
    }

    private boolean isPartiallySubmerged() {
        Block feet = level.getBlock(getFloorX(), getFloorY(), getFloorZ());
        Block head = level.getBlock(getFloorX(), NukkitMath.floorDouble(y + getHeight() * 0.8), getFloorZ());
        return (feet instanceof BlockLiquid) && !(head instanceof BlockLiquid);
    }

    private boolean isFullySubmerged() {
        Block feet = level.getBlock(getFloorX(), getFloorY(), getFloorZ());
        Block head = level.getBlock(getFloorX(), NukkitMath.floorDouble(y + getHeight() * 0.8), getFloorZ());
        return (feet instanceof BlockLiquid) && (head instanceof BlockLiquid);
    }

    protected boolean checkJump(double dx, double dz) {
        if (isFullySubmerged()) {
            if (this.motionY == this.getGravity() * 2) {
                return this.canSwimIn(level.getBlockIdAt(chunk,
                        NukkitMath.floorDouble(this.x), (int) this.y,
                        NukkitMath.floorDouble(this.z)));
            } else {
                if (this.canSwimIn(level.getBlockIdAt(chunk,
                        NukkitMath.floorDouble(this.x), (int) (this.y + 0.8),
                        NukkitMath.floorDouble(this.z)))) {
                    if (!(this.isDrowned || this instanceof EntityIronGolem) || this.target == null) {
                        this.motionY = this.getGravity() * 2;
                    }
                    return true;
                }
            }
            return false;
        }

        if (isPartiallySubmerged()) {
            if (!this.onGround) return false;
            if (this.motionY <= this.getGravity() * 2) { this.motionY = this.getGravity() * 2; return true; }
            return false;
        }

        if (!this.onGround || this.stayTime > 0) return false;

        Block that = level.getBlock(new Vector3(
                NukkitMath.floorDouble(this.x + dx), (int) this.y,
                NukkitMath.floorDouble(this.z + dz)));
        Block block = that.getSide(this.getHorizontalFacing());

        if (isJumpObstacle(block)) {
            Block blockAbove = block.up();
            Block thatAbove2 = that.up(2);
            int bx = block.getFloorX(), by = block.getFloorY(), bz = block.getFloorZ();

            if (canEntityPassThrough(blockAbove, bx, by + 1, bz) &&
                    canEntityPassThrough(thatAbove2, that.getFloorX(), that.getFloorY() + 2, that.getFloorZ())) {
                if (this.motionY <= this.getGravity() * 2) { this.motionY = this.getGravity() * 2; return true; }
            }
        }
        return false;
    }

    private boolean isJumpObstacle(Block block) {
        AxisAlignedBB bb = getBlockBB(block.getFloorX(), block.getFloorY(), block.getFloorZ());
        if (bb == null) return false;

        double blockHeight = bb.getMaxY() - bb.getMinY();

        if (blockHeight > 1.0) return false;

        if (blockHeight < 0.2) return false;

        double blockWidthX = bb.getMaxX() - bb.getMinX();
        double blockWidthZ = bb.getMaxZ() - bb.getMinZ();
        if (blockWidthX < 0.8 && blockWidthZ < 0.8) return false;

        if (blockHeight >= 0.2 && blockHeight < 1.0) return true;

        return block.isSolid() &&
                !canEntityPassThrough(block, block.getFloorX(), block.getFloorY(), block.getFloorZ());
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        if (!this.isInTickingRange()) return null;

        if (this.isMovement() && !isImmobile()) {
            if (this.isKnockback()) { handleKnockbackMovement(); return null; }

            updateGroundState();

            if (this.getServer().getSettings().world().entity().mobAi()) {
                aiUpdateInterval++;
                if (aiUpdateInterval >= AI_UPDATE_EVERY) {
                    handleAIMovement();
                    aiUpdateInterval = 0;
                }
            }

            performMovement(tickDiff);
            cleanupCachePeriodically();
            return this.target;
        }
        return null;
    }

    private void handleKnockbackMovement() {
        this.move(this.motionX, this.motionY, this.motionZ);
        if (this.isDrowned && this.isInsideOfWater()) this.motionY -= this.getGravity() * 0.3;
        else                                           this.motionY -= this.getGravity();
        this.updateMovement();
    }

    private void updateGroundState() {
        Block levelBlock = getLevelBlock();
        boolean inWater = levelBlock.getId() == 8 || levelBlock.getId() == 9;

        int   downId    = level.getBlockIdAt(chunk, getFloorX(), getFloorY() - 1, getFloorZ());
        Block downBlock = level.getBlock(getFloorX(), getFloorY() - 1, getFloorZ());

        if (inWater && (downId == 0 || downId == 8 || downId == 9 ||
                downId == BlockID.LAVA || downId == BlockID.STILL_LAVA ||
                downId == BlockID.SIGN_POST || downId == BlockID.WALL_SIGN)) {
            onGround = false; return;
        }

        if (downId == 0 || downId == BlockID.SIGN_POST || downId == BlockID.WALL_SIGN) {
            onGround = false; return;
        }

        AxisAlignedBB blockBB = downBlock.getBoundingBox();
        if (blockBB != null) {
            onGround = (this.y - getFloorY()) <= blockBB.getMaxY() + 0.1;
        } else {
            onGround = false;
        }
    }

    private boolean isWaterAhead(double dx, double dz) {
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.01) return false;

        int checkX = NukkitMath.floorDouble(this.x + dx / dist * 1.5);
        int checkZ = NukkitMath.floorDouble(this.z + dz / dist * 1.5);
        int checkY = this.getFloorY();

        Block ahead = level.getBlock(checkX, checkY, checkZ);
        Block below = level.getBlock(checkX, checkY - 1, checkZ);

        return (below instanceof BlockLiquid || below instanceof BlockLava ||
                ahead instanceof BlockLiquid || ahead instanceof BlockLava);
    }

    private boolean isHighBlockAhead(double dx, double dz) {
        if (dx == 0 && dz == 0) return false;

        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.01) return false;

        double dirX = dx / dist;
        double dirZ = dz / dist;

        for (double checkDist = 0.5; checkDist <= 1.5; checkDist += 0.5) {
            int checkX = NukkitMath.floorDouble(this.x + dirX * checkDist);
            int checkZ = NukkitMath.floorDouble(this.z + dirZ * checkDist);
            int checkY = this.getFloorY();

            Block ahead = level.getBlock(checkX, checkY, checkZ);
            if (ahead.getId() != Block.AIR && !ahead.canPassThrough()) {
                AxisAlignedBB blockBB = getBlockBB(checkX, checkY, checkZ);

                if (blockBB != null) {
                    double blockHeight = blockBB.getMaxY() - blockBB.getMinY();
                    if (blockHeight > 1.0) {
                        return true;
                    }
                }
            }

            Block headBlock = level.getBlock(checkX, checkY + 1, checkZ);
            if (headBlock.getId() != Block.AIR && !headBlock.canPassThrough()) {
                AxisAlignedBB headBB = getBlockBB(checkX, checkY + 1, checkZ);

                if (headBB != null) {
                    double headBlockHeight = headBB.getMaxY() - headBB.getMinY();
                    if (headBlockHeight > 1.0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void handleAIMovement() {
        this.moveMultiplier = 1.0f;

        if (this.followTarget != null && !this.followTarget.closed &&
                this.followTarget.isAlive() && this.followTarget.canBeFollowed()) {

            double dist = this.distance(this.followTarget);
            this.isCloseToTarget = dist <= 0.3;

            if (this.isCloseToTarget) {
                lookAtTargetTicks = 20;
                moveCloseToTarget(this.followTarget);
                lookAtTargetWithHead(this.followTarget);
            } else {
                lookAtTargetTicks = 0;
                boolean los      = canSeeTarget(this.followTarget);
                boolean stuck    = isStuckMovingToTarget();
                boolean hasPath  = !currentPath.isEmpty() && !isWaitingForPath;
                double  dx = this.followTarget.x - this.x, dz = this.followTarget.z - this.z;
                boolean water    = isWaterAhead(dx, dz);
                boolean noSolidGround = isNoSolidGroundAhead(dx, dz);
                boolean highBlock = isHighBlockAhead(dx, dz);

                if (los && !stuck && !hasPath && !water && !noSolidGround && !highBlock) {
                    clearPath(); moveDirectlyTo(this.followTarget); lookAtTargetWithHead(this.followTarget);
                } else {
                    if (shouldRecalculatePath(this.followTarget) || stuck || water || noSolidGround || highBlock)
                        findPathToTarget(this.followTarget);

                    if (!isWaitingForPath && !currentPath.isEmpty()) {
                        followPath();
                        if (pathIndex < currentPath.size()) lookAtPosition(currentPath.get(pathIndex));
                    } else if (!isWaitingForPath && !water && !noSolidGround && !highBlock) {
                        moveDirectlyTo(this.followTarget);
                    }
                }
            }

            if (!this.isCloseToTarget && this.followTarget instanceof Entity &&
                    canSeeTarget(this.followTarget)) {
                lookAtTargetWithHead(this.followTarget);
            }

            if (repathCooldown > 0) repathCooldown--;
            return;
        }

        targetCheckInterval++;
        Vector3 before = this.target;

        if (targetCheckInterval >= TARGET_CHECK_EVERY) {
            this.checkTarget(); targetCheckInterval = 0;
        }

        if (this.target instanceof EntityCreature || before != this.target) {
            if (this.target instanceof Entity) {
                double dist = this.distance(this.target);
                this.isCloseToTarget = dist <= 0.2;

                if (this.isCloseToTarget) {
                    lookAtTargetTicks = 20;
                    moveCloseToTarget(this.target); lookAtTargetWithHead(this.target);
                    if (repathCooldown > 0) repathCooldown--;
                    return;
                }
                lookAtTargetTicks = 0;
            }

            boolean los     = this.target instanceof Entity && canSeeTarget(this.target);
            boolean stuck   = isStuckMovingToTarget();
            boolean hasPath = !currentPath.isEmpty() && !isWaitingForPath;
            double  dx = this.target.x - this.x, dz = this.target.z - this.z;
            boolean water   = isWaterAhead(dx, dz);
            boolean noSolidGround = isNoSolidGroundAhead(dx, dz);
            boolean highBlock = isHighBlockAhead(dx, dz);

            if (los && !stuck && !hasPath && !water && !noSolidGround && !highBlock) {
                clearPath(); moveDirectlyTo(this.target);
                if (this.motionX != 0 || this.motionZ != 0) updateBodyYaw(this.motionX, this.motionZ);
                lookAtTargetWithHead(this.target);
            } else {
                if (this.target != null && (shouldRecalculatePath(this.target) || stuck || water || noSolidGround || highBlock))
                    findPathToTarget(this.target);

                if (!isWaitingForPath && !currentPath.isEmpty()) {
                    followPath();
                    if (pathIndex < currentPath.size()) lookAtPosition(currentPath.get(pathIndex));
                } else if (!isWaitingForPath && !water && !noSolidGround && !highBlock) {
                    moveDirectlyTo(this.target);
                    if (this.motionX != 0 || this.motionZ != 0) updateBodyYaw(this.motionX, this.motionZ);
                }

                if (this.target instanceof Entity && !this.isCloseToTarget && canSeeTarget(this.target))
                    lookAtTargetWithHead(this.target);
            }

            if (repathCooldown > 0) repathCooldown--;
        } else {
            if (this.target == null) {
                this.motionX = 0; this.motionZ = 0; this.moveTime = 0;
                tryIdleWander();
            }
            if (this.motionX != 0 || this.motionZ != 0) updateBodyYaw(this.motionX, this.motionZ);
        }
    }

    private boolean isNoSolidGroundAhead(double dx, double dz) {
        if (dx == 0 && dz == 0) return false;

        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.01) return false;

        int checkX = NukkitMath.floorDouble(this.x + dx / dist * 1.5);
        int checkZ = NukkitMath.floorDouble(this.z + dz / dist * 1.5);
        int checkY = this.getFloorY();

        Block below = level.getBlock(checkX, checkY - 1, checkZ);

        return !isBlockWalkable(below);
    }

    private void performMovement(int tickDiff) {
        if (lookAtTargetTicks > 0 && !this.isCloseToTarget &&
                this.target instanceof Entity && canSeeTarget(this.target)) {
            lookAtTargetWithHead(this.target); lookAtTargetTicks--;
        }

        Block   levelBlock = getLevelBlock();
        boolean inWater    = levelBlock instanceof BlockLiquid;

        if (inWater) {
            this.motionX = 0; this.motionZ = 0; this.motionY = -0.2;
            if (this.age % 10 == 0) this.motionY -= 0.05;
            this.move(0, this.motionY, 0);
            if (this.followTarget != null) lookAtTargetWithHead(this.followTarget);
            else if (this.target != null)  lookAtTargetWithHead(this.target);
        } else {
            double  dx     = this.motionX, dz = this.motionZ;
            boolean isJump = this.checkJump(dx, dz);

            if (this.stayTime > 0) {
                this.stayTime -= tickDiff; this.move(0, this.motionY, 0);
            } else {
                Vector2 be = new Vector2(this.x + dx, this.z + dz);
                this.move(dx, this.motionY, dz);
                Vector2 af = new Vector2(this.x, this.z);
                if ((be.x != af.x || be.y != af.y) && !isJump) this.moveTime -= 10;
            }

            if (!isJump) {
                if (this.onGround)                              this.motionY = 0;
                else if (this.motionY > -this.getGravity() * 4) this.motionY -= this.getGravity();
                else                                             this.motionY -= this.getGravity();
            }
        }

        this.updateMovement();
    }

    private void lookAtPosition(Vector3 position) {
        double dx = position.x - this.x, dz = position.z - this.z;
        double dy = position.y - (this.y + this.getEyeHeight());
        double distXZ = Math.sqrt(dx * dx + dz * dz);

        double yaw   = (Math.toDegrees(-Math.atan2(dx, dz)) + 360) % 360;
        double pitch = Math.max(-60, Math.min(60, Math.toDegrees(-Math.atan2(dy, distXZ))));

        this.setHeadYaw((float) yaw); this.setPitch((float) pitch);
    }

    private void moveCloseToTarget(Vector3 target) {
        double dx = target.x - this.x, dz = target.z - this.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.1) { this.motionX = 0; this.motionZ = 0; return; }

        double speed = this.getSpeed() * moveMultiplier * 0.08;
        this.motionX = speed * (dx / dist);
        this.motionZ = speed * (dz / dist);
    }

    private void lookAtTargetWithHead(Vector3 target) {
        if (!(target instanceof Entity)) return;

        double dx = target.x - this.x, dz = target.z - this.z;
        double dy = (target.y + ((Entity) target).getHeight() * 0.5) - (this.y + this.getEyeHeight());
        double distXZ = Math.sqrt(dx * dx + dz * dz);

        double yaw   = (Math.toDegrees(-Math.atan2(dx, dz)) + 360) % 360;
        double pitch = Math.max(-60, Math.min(60, Math.toDegrees(-Math.atan2(dy, distXZ))));

        this.setHeadYaw((float) yaw); this.setPitch((float) pitch);
    }

    @Override
    protected void followPath() {
        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) return;

        Vector3 nextPoint = currentPath.get(pathIndex);

        if (isBlockedForPath(nextPoint)) { clearPath(); this.target = null; return; }

        double x = nextPoint.x - this.x, z = nextPoint.z - this.z;
        double diff = Math.sqrt(x * x + z * z);

        if (diff > 0.1) {
            this.motionX = this.getSpeed() * moveMultiplier * 0.1 * (x / diff);
            this.motionZ = this.getSpeed() * moveMultiplier * 0.1 * (z / diff);
            updateBodyYaw(x, z);
        }

        if (this.distance(nextPoint) < 0.8) pathIndex++;
    }

    @Override
    protected void moveDirectlyTo(Vector3 target) {
        if (target == null || isBlockedForPath(target)) {
            this.motionX = 0;
            this.motionZ = 0;
            return;
        }

        double x = target.x - this.x, z = target.z - this.z;
        double diff = Math.sqrt(x * x + z * z);

        if (isHighBlockAhead(x, z)) {
            this.motionX = 0;
            this.motionZ = 0;
            findPathToTarget(target);
            return;
        }

        if (diff > 0.1) {
            this.motionX = this.getSpeed() * moveMultiplier * 0.15 * (x / diff);
            this.motionZ = this.getSpeed() * moveMultiplier * 0.15 * (z / diff);
            updateBodyYaw(x, z);
        } else {
            this.motionX = 0; this.motionZ = 0;
        }
    }

    @Override
    protected void updateYaw(double x, double z) { updateBodyYaw(x, z); }

    protected void updateBodyYaw(double x, double z) {
        this.setYaw((float) Math.toDegrees(-Math.atan2(x, z)));
        this.setPitch(0f);
    }

    private static long packPosition(int x, int y, int z) {
        return ((long) x & 0x3FFFFFF) | (((long) y & 0x3FFFFFF) << 26) | (((long) z & 0x3FFFFFF) << 52);
    }

    private static void cleanupCachePeriodically() {
        cacheCleanupCounter++;
        if (cacheCleanupCounter >= CACHE_CLEANUP_INTERVAL) {
            if (reachableCache.size()  > CACHE_MAX_SIZE) reachableCache.clear();
            if (visibilityCache.size() > CACHE_MAX_SIZE) visibilityCache.clear();
            cacheCleanupCounter = 0;
        }
    }
    private double getEntityWidth() {
        return this.getWidth();
    }

    private double getEntityHeight() {
        return this.getHeight();
    }
}