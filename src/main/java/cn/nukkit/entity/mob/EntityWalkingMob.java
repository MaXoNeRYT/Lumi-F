package cn.nukkit.entity.mob;

import cn.nukkit.Difficulty;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityWalking;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.Objects;
import java.util.Optional;

public abstract class EntityWalkingMob extends EntityWalking implements EntityMob {

    private int[] minDamage;
    private int[] maxDamage;
    private boolean canAttack = true;
    public long isAngryTo = -1;

    public EntityWalkingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void setTarget(Entity target) {
        this.setTarget(target, true);
    }

    public void setTarget(Entity target, boolean attack) {
        super.setTarget(target);
        this.canAttack = attack;
    }

    @Override
    public int getDamage() {
        return getDamage(null);
    }

    @Override
    public int getDamage(Integer difficulty) {
        return Utils.rand(this.getMinDamage(difficulty), this.getMaxDamage(difficulty));
    }

    @Override
    public int getMinDamage() {
        return getMinDamage(null);
    }

    @Override
    public int getMinDamage(Integer difficulty) {
        if (difficulty == null || difficulty > 3 || difficulty < 0) {
            difficulty = this.server.getDifficulty().getId();
        }
        return this.minDamage[difficulty];
    }

    @Override
    public int getMaxDamage() {
        return getMaxDamage(null);
    }

    @Override
    public int getMaxDamage(Integer difficulty) {
        if (difficulty == null || difficulty > 3 || difficulty < 0) {
            difficulty = this.server.getDifficulty().getId();
        }
        return this.maxDamage[difficulty];
    }

    @Override
    public void setDamage(int damage) {
        this.setDamage(damage, this.server.getDifficulty().getId());
    }

    @Override
    public void setDamage(int damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            this.minDamage[difficulty] = damage;
            this.maxDamage[difficulty] = damage;
        }
    }

    @Override
    public void setDamage(int[] damage) {
        if (damage.length != 4) {
            throw new IllegalArgumentException("Invalid damage array length");
        }

        if (minDamage == null || minDamage.length < 4) {
            minDamage = Utils.getEmptyDamageArray();
        }

        if (maxDamage == null || maxDamage.length < 4) {
            maxDamage = Utils.getEmptyDamageArray();
        }

        for (int i = 0; i < 4; i++) {
            this.minDamage[i] = damage[i];
            this.maxDamage[i] = damage[i];
        }
    }

    @Override
    public void setMinDamage(int[] damage) {
        if (damage.length != 4) {
            throw new IllegalArgumentException("Invalid damage array length");
        }

        for (int i = 0; i < 4; i++) {
            this.setMinDamage(damage[i], i);
        }
    }

    @Override
    public void setMinDamage(int damage) {
        this.setMinDamage(damage, this.server.getDifficulty().getId());
    }

    @Override
    public void setMinDamage(int damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            this.minDamage[difficulty] = Math.min(damage, this.getMaxDamage(difficulty));
        }
    }

    @Override
    public void setMaxDamage(int[] damage) {
        if (damage.length != 4) {
            throw new IllegalArgumentException("Invalid damage array length");
        }

        for (int i = 0; i < 4; i++) {
            this.setMaxDamage(damage[i], i);
        }
    }

    @Override
    public void setMaxDamage(int damage) {
        this.setMaxDamage(damage, this.server.getDifficulty().getId());
    }

    @Override
    public void setMaxDamage(int damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            this.maxDamage[difficulty] = Math.max(damage, this.getMinDamage(difficulty));
        }
    }


    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.server.getDifficulty() == Difficulty.PEACEFUL) {
            this.close();
            return false;
        }

        if (!this.isAlive()) {
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        if (this.target != null) {
            this.pushEntities();
        }

        Vector3 target = this.updateMove(tickDiff);
        if (Objects.nonNull(target)) {
            Optional.ofNullable(getAttackTarget(target))
                    .ifPresent(this::attackEntity);
        }
        return true;
    }

    @Override
    protected Entity getAttackTarget(Vector3 target) {
        if (isMeetAttackConditions(target)) {
            Entity entity = (Entity) target;
            if (!entity.isClosed() && (target != this.followTarget || this.canAttack)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean isMeetAttackConditions(Vector3 target) {
        return this.getServer().getSettings().world().entity().mobAi() &&
                target instanceof EntityCreature &&
                (!this.isFriendly() || !(target instanceof Player) || ((Entity) target).getId() == this.isAngryTo);
    }

    protected void pushEntities() {
        double radius = 1.5;
        for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(radius, 0.5, radius), this)) {
            if (entity instanceof EntityWalkingMob && entity != this && entity.isAlive()) {
                double dx = this.x - entity.x;
                double dz = this.z - entity.z;
                double distance = Math.sqrt(dx * dx + dz * dz);

                if (distance < 0.01) continue;

                double strength = 0.15;
                double force = strength * (1.0 - (distance / radius));

                dx /= distance;
                dz /= distance;

                entity.addMotion(dx * force, 0, dz * force);
            }
        }
    }
}