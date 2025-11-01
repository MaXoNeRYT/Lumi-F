package cn.nukkit.entity;

import cn.nukkit.block.*;
import cn.nukkit.entity.passive.EntityIronGolem;
import cn.nukkit.entity.passive.EntitySkeletonHorse;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public abstract class EntityWalking extends PathfindingMob {

    private int collisionTicks = 0;
    private boolean isCloseToTarget = false;
    private int lookAtTargetTicks = 0;

    public EntityWalking(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected void checkTarget() {
        if (this.isKnockback()) return;

        if (this.followTarget != null && !this.followTarget.closed &&
                this.followTarget.isAlive() && this.followTarget.canBeFollowed()) {
            return;
        }

        Vector3 target = this.target;
        if (!(target instanceof EntityCreature) ||
                (!((EntityCreature) target).closed && !this.targetOption((EntityCreature) target, this.distanceSquared(target))) ||
                !((Entity) target).canBeFollowed()) {

            double near = Integer.MAX_VALUE;
            for (Entity entity : this.getLevel().getEntities()) {
                if (entity == this || !(entity instanceof EntityCreature creature) ||
                        entity.closed || !this.canTarget(entity)) continue;

                if (creature instanceof BaseEntity baseEntity &&
                        baseEntity.isFriendly() == this.isFriendly() && !this.isInLove()) continue;

                double distance = this.distanceSquared(creature);
                if (distance > near || !this.targetOption(creature, distance)) continue;

                near = distance;
                this.stayTime = 0;
                this.moveTime = 0;
                this.target = creature;
            }
        }

        if (this.target instanceof EntityCreature && !((EntityCreature) this.target).closed &&
                ((EntityCreature) this.target).isAlive() &&
                this.targetOption((EntityCreature) this.target, this.distanceSquared(this.target))) {
            return;
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
            final int fX = Utils.rand() ? x : -x;
            final double fY = Utils.rand(-10.0, 10.0) / 10;
            final int fZ = Utils.rand() ? z : -z;

            if (canReachPosition(this.add(fX, fY, fZ))) {
                this.target = this.add(fX, fY, fZ);
                break;
            }
        }
    }

    private boolean canReachPosition(Vector3 pos) {
        int x = NukkitMath.floorDouble(pos.x);
        int y = NukkitMath.floorDouble(pos.y);
        int z = NukkitMath.floorDouble(pos.z);

        Block block = level.getBlock(x, y, z);
        Block below = level.getBlock(x, y - 1, z);
        Block above = level.getBlock(x, y + 1, z);

        boolean isBelowWalkable = below.isSolid() ||
                below instanceof BlockStairs ||
                below instanceof BlockSlab ||
                below instanceof BlockCarpet ||
                below instanceof BlockMossCarpet;

        boolean isBlockPassable = block.canPassThrough() ||
                block instanceof BlockStairs ||
                block instanceof BlockSlab ||
                block instanceof BlockCarpet ||
                block instanceof BlockMossCarpet;

        boolean isAbovePassable = above.canPassThrough() ||
                above instanceof BlockSlab ||
                above instanceof BlockCarpet ||
                above instanceof BlockMossCarpet;

        if (isBlockPassable && isBelowWalkable && isAbovePassable) return true;

        return level.getBlock(x, y + 1, z).canPassThrough() &&
                level.getBlock(x, y + 2, z).canPassThrough() &&
                (block.isSolid() || block instanceof BlockStairs || block instanceof BlockSlab);
    }

    private boolean canSeeTarget(Vector3 target) {
        if (!(target instanceof Entity)) return false;

        double distance = this.distance(target);
        if (distance > 16) {
            return false;
        }

        double dx = target.x - this.x;
        double dy = (target.y + ((Entity) target).getHeight() * 0.5) - (this.y + this.getEyeHeight());
        double dz = target.z - this.z;

        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.1) return true;

        dx /= length;
        dy /= length;
        dz /= length;

        return canSeePosition(this.x, this.y + this.getEyeHeight(), this.z,
                target.x, target.y + ((Entity) target).getHeight() * 0.5, target.z);
    }

    private boolean canSeePosition(double startX, double startY, double startZ,
                                   double endX, double endY, double endZ) {
        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance < 0.1) return true;

        dx /= distance;
        dy /= distance;
        dz /= distance;

        int steps = (int) (distance / 0.5) + 1;
        for (int i = 1; i < steps; i++) {
            double checkX = startX + dx * i * 0.5;
            double checkY = startY + dy * i * 0.5;
            double checkZ = startZ + dz * i * 0.5;

            Block block = level.getBlock(NukkitMath.floorDouble(checkX),
                    NukkitMath.floorDouble(checkY),
                    NukkitMath.floorDouble(checkZ));

            if (!block.canPassThrough() && block.isSolid() &&
                    !(block instanceof BlockFence) &&
                    !(block instanceof BlockFenceGate) &&
                    !(block instanceof BlockStairs) &&
                    !(block instanceof BlockSlab)) {
                return false;
            }

            Block blockAbove = level.getBlock(NukkitMath.floorDouble(checkX),
                    NukkitMath.floorDouble(checkY) + 1,
                    NukkitMath.floorDouble(checkZ));
            if (!blockAbove.canPassThrough() && blockAbove.isSolid() &&
                    !(blockAbove instanceof BlockFence) &&
                    !(blockAbove instanceof BlockFenceGate)) {
                return false;
            }
        }

        return true;
    }

    protected boolean checkJump(double dx, double dz) {
        if (this.motionY == this.getGravity() * 2) {
            return this.canSwimIn(level.getBlockIdAt(chunk, NukkitMath.floorDouble(this.x), (int) this.y, NukkitMath.floorDouble(this.z)));
        } else {
            if (this.canSwimIn(level.getBlockIdAt(chunk, NukkitMath.floorDouble(this.x), (int) (this.y + 0.8), NukkitMath.floorDouble(this.z)))) {
                if (!(this.isDrowned || this instanceof EntityIronGolem || this instanceof EntitySkeletonHorse) || this.target == null) {
                    this.motionY = this.getGravity() * 2;
                }
                return true;
            }
        }

        if (!this.onGround || this.stayTime > 0) return false;

        Block that = this.getLevel().getBlock(new Vector3(
                NukkitMath.floorDouble(this.x + dx),
                (int) this.y,
                NukkitMath.floorDouble(this.z + dz)
        ));

        Block block = that.getSide(this.getHorizontalFacing());

        if (isJumpObstacle(block)) {
            Block blockAbove = block.up();
            Block thatAbove2 = that.up(2);

            if (blockAbove.canPassThrough() && thatAbove2.canPassThrough()) {
                if (this.motionY <= this.getGravity() * 2) {
                    this.motionY = this.getGravity() * 2;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isJumpObstacle(Block block) {
        if (block instanceof BlockCarpet || block instanceof BlockMossCarpet) {
            return false;
        }

        if (block instanceof BlockFence || block instanceof BlockWall) {
            return false;
        }

        if (block instanceof BlockSlab || block instanceof BlockStairs) {
            return true;
        }

        return !block.canPassThrough() && block.isSolid();
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        if (!this.isInTickingRange()) return null;

        if (this.isMovement() && !isImmobile()) {
            if (this.isKnockback()) {
                handleKnockbackMovement();
                return null;
            }

            updateGroundState();

            if (this.getServer().getSettings().world().entity().mobAi()) {
                handleAIMovement();
            }

            performMovement(tickDiff);
            return this.target;
        }
        return null;
    }

    private void handleKnockbackMovement() {
        this.move(this.motionX, this.motionY, this.motionZ);
        if (this.isDrowned && this.isInsideOfWater()) {
            this.motionY -= this.getGravity() * 0.3;
        } else {
            this.motionY -= this.getGravity();
        }
        this.updateMovement();
    }

    private void updateGroundState() {
        Block levelBlock = getLevelBlock();
        boolean inWater = levelBlock.getId() == 8 || levelBlock.getId() == 9;
        int downId = level.getBlockIdAt(chunk, getFloorX(), getFloorY() - 1, getFloorZ());
        Block downBlock = level.getBlock(getFloorX(), getFloorY() - 1, getFloorZ());

        if (inWater && (downId == 0 || downId == 8 || downId == 9 || downId == BlockID.LAVA ||
                downId == BlockID.STILL_LAVA || downId == BlockID.SIGN_POST || downId == BlockID.WALL_SIGN)) {
            onGround = false;
        }

        if ((downId == 0 || downId == BlockID.SIGN_POST || downId == BlockID.WALL_SIGN) &&
                !(downBlock instanceof BlockSlab) && !(downBlock instanceof BlockStairs)) {
            onGround = false;
        } else {
            if (downBlock instanceof BlockSlab || downBlock instanceof BlockStairs) {
                double blockTop = downBlock.getBoundingBox() != null ? downBlock.getBoundingBox().getMaxY() : 1.0;
                double feetY = this.y - getFloorY();
                onGround = feetY <= blockTop + 0.1;
            } else if (downBlock instanceof BlockCarpet || downBlock instanceof BlockMossCarpet) {
                onGround = true;
            } else {
                onGround = true;
            }
        }
    }

    private void handleAIMovement() {
        if (this.followTarget != null && !this.followTarget.closed &&
                this.followTarget.isAlive() && this.followTarget.canBeFollowed()) {

            double distanceToTarget = this.distance(this.followTarget);
            this.isCloseToTarget = distanceToTarget <= 2.5;

            if (this.isCloseToTarget) {
                lookAtTargetTicks = 20;
                moveCloseToTarget(this.followTarget);
                if (canSeeTarget(this.followTarget)) {
                    lookAtTargetWithHead(this.followTarget);
                }
            } else {
                lookAtTargetTicks = 0;
                if (shouldRecalculatePath(this.followTarget)) {
                    findPathToTarget(this.followTarget);
                }

                if (!isWaitingForPath && !currentPath.isEmpty()) {
                    followPath();
                } else if (!isWaitingForPath) {
                    moveDirectlyTo(this.followTarget);
                }
            }

            if (this.followTarget instanceof Entity && canSeeTarget(this.followTarget)) {
                lookAtTargetWithHead(this.followTarget);
            }

            if (repathCooldown > 0) repathCooldown--;
            return;
        }

        Vector3 before = this.target;
        this.checkTarget();

        if (this.target instanceof EntityCreature || before != this.target) {
            if (this.target instanceof Entity) {
                double distanceToTarget = this.distance(this.target);
                this.isCloseToTarget = distanceToTarget <= 2.5;

                if (this.isCloseToTarget) {
                    lookAtTargetTicks = 20;
                    moveCloseToTarget(this.target);
                    if (canSeeTarget(this.target)) {
                        lookAtTargetWithHead(this.target);
                    }

                    if (repathCooldown > 0) repathCooldown--;
                    return;
                } else {
                    lookAtTargetTicks = 0;
                }
            }

            if (shouldRecalculatePath(this.target)) {
                findPathToTarget(this.target);
            }

            if (!isWaitingForPath && !currentPath.isEmpty()) {
                followPath();
            } else if (!isWaitingForPath) {
                if (this.stayTime <= 0 || this.distance(this.target) > (this.getWidth() / 2 + 0.5)) {
                    moveDirectlyTo(this.target);
                } else {
                    this.motionX = 0;
                    this.motionZ = 0;
                }
            }

            if (this.target instanceof Entity && canSeeTarget(this.target)) {
                lookAtTargetWithHead(this.target);
            }

            if (repathCooldown > 0) repathCooldown--;
        }
    }

    private void moveCloseToTarget(Vector3 target) {
        double dx = target.x - this.x;
        double dz = target.z - this.z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        double stopDistance = 0.1;
        if (distance < stopDistance) {
            this.motionX = 0;
            this.motionZ = 0;
        } else {
            double speed = this.getSpeed() * moveMultiplier * 0.08;
            this.motionX = speed * (dx / distance);
            this.motionZ = speed * (dz / distance);
        }
    }

    private void lookAtTargetWithHead(Vector3 target) {
        if (!(target instanceof Entity)) return;

        double dx = target.x - this.x;
        double dz = target.z - this.z;

        double targetY = target.y + ((Entity) target).getHeight() * 0.5;
        double dy = targetY - (this.y + this.getEyeHeight());

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        double yaw = Math.toDegrees(-Math.atan2(dx, dz));
        yaw = (yaw + 360) % 360;

        double pitch = Math.toDegrees(-Math.atan2(dy, distanceXZ));
        pitch = Math.max(-60, Math.min(60, pitch));

        this.setHeadYaw((float) yaw);
        this.setPitch((float) pitch);
    }

    private void performMovement(int tickDiff) {
        if (lookAtTargetTicks > 0 && this.target instanceof Entity && canSeeTarget(this.target)) {
            lookAtTargetWithHead(this.target);
            lookAtTargetTicks--;
        }

        double dx = this.motionX;
        double dz = this.motionZ;
        boolean isJump = this.checkJump(dx, dz);

        Block levelBlock = getLevelBlock();
        boolean inWater = levelBlock.getId() == 8 || levelBlock.getId() == 9;

        if (this.stayTime > 0 && !inWater) {
            this.stayTime -= tickDiff;
            this.move(0, this.motionY, 0);
        } else {
            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, this.motionY, dz);
            Vector2 af = new Vector2(this.x, this.z);

            if ((be.x != af.x || be.y != af.y) && !isJump) {
                this.moveTime -= 10;
            }
        }

        if (!isJump) {
            if (this.onGround && !inWater) {
                this.motionY = 0;
            } else if (this.motionY > -this.getGravity() * 4) {
                if (!(this.level.getBlock(NukkitMath.floorDouble(this.x), (int) (this.y + 0.8), NukkitMath.floorDouble(this.z)) instanceof BlockLiquid)) {
                    this.motionY -= this.getGravity();
                }
            } else {
                this.motionY -= this.getGravity();
            }
        }

        this.updateMovement();
    }

    @Override
    protected void followPath() {
        if (currentPath.isEmpty() || pathIndex >= currentPath.size()) return;

        Vector3 nextPoint = currentPath.get(pathIndex);
        double x = nextPoint.x - this.x;
        double z = nextPoint.z - this.z;
        double diff = Math.sqrt(x * x + z * z);

        if (diff > 0.1) {
            this.motionX = this.getSpeed() * moveMultiplier * 0.1 * (x / diff);
            this.motionZ = this.getSpeed() * moveMultiplier * 0.1 * (z / diff);

            updateBodyYaw(x, z);
        }

        if (this.distance(nextPoint) < 0.8) {
            pathIndex++;
        }
    }

    @Override
    protected void moveDirectlyTo(Vector3 target) {
        double x = target.x - this.x;
        double z = target.z - this.z;
        double diff = Math.sqrt(x * x + z * z);

        double stopDistance = 0.1;
        if (diff > stopDistance) {
            this.motionX = this.getSpeed() * moveMultiplier * 0.1 * (x / diff);
            this.motionZ = this.getSpeed() * moveMultiplier * 0.1 * (z / diff);

            updateBodyYaw(x, z);
        } else {
            this.motionX = 0;
            this.motionZ = 0;
        }
    }

    @Override
    protected void updateYaw(double x, double z) {
        updateBodyYaw(x, z);
    }

    protected void updateBodyYaw(double x, double z) {
        double yaw = Math.toDegrees(-Math.atan2(x, z));
        this.setYaw((float) yaw);
        this.setPitch(0f);
    }
}