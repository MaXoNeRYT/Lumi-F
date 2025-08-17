package cn.nukkit.dispenser.impl;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class ProjectileDispenseBehavior extends DefaultDispenseBehavior {

    private final String entityType;

    public ProjectileDispenseBehavior(String entity) {
        this.entityType = entity;
    }

    @Override
    public Item dispense(BlockDispenser source, BlockFace face, Item item) {
        Vector3 dispensePos = source.getDispensePosition();

        CompoundTag nbt = this.correctNBT(Entity.getDefaultNBT(dispensePos), item);
        Entity projectile = Entity.createEntity(entityType, source.level.getChunk(dispensePos.getChunkX(), dispensePos.getChunkZ()), nbt);

        if (!(projectile instanceof EntityProjectile entity)) {
            return super.dispense(source, face, item);
        }

        Vector3 motion = new Vector3(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset())
                .normalize();

        projectile.setMotion(motion);
        entity.inaccurate(getAccuracy());
        projectile.setMotion(projectile.getMotion().multiply(getMotion()));

        entity.updateRotation();

        if (projectile instanceof EntityThrownTrident thrownTrident) {
            item.setDamage(item.getDamage() + 1);
            thrownTrident.setItem(item);
        }

        projectile.spawnToAll();
        return null;
    }

    protected double getMotion() {
        return 1.1;
    }

    protected float getAccuracy() {
        return 6;
    }

    protected CompoundTag correctNBT(CompoundTag nbt) {
        return this.correctNBT(nbt, null);
    }

    protected CompoundTag correctNBT(CompoundTag nbt, Item item) {
        if (item != null) {
            if (item.getId() == Item.SPLASH_POTION || item.getId() == Item.LINGERING_POTION) {
                nbt.putInt("PotionId", item.getDamage());
            }
        }
        return nbt;
    }
}
