package cn.nukkit.dispenser.impl;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class FireChargeDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 dispensePos = block.getDispensePosition();

        Entity projectile = Entity.createEntity("SmallFireBall", block.level.getChunk(dispensePos.getChunkX(), dispensePos.getChunkZ()), Entity.getDefaultNBT(dispensePos));

        if (!(projectile instanceof EntityProjectile entity)) {
            return super.dispense(block, face, item);
        }

        projectile.setMotion(new Vector3(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset()).normalize().multiply(1.3));
        entity.inaccurate(6f);
        entity.updateRotation();

        projectile.spawnToAll();
        return null;
    }
}
