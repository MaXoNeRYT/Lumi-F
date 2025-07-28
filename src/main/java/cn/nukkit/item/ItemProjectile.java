package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public abstract class ItemProjectile extends Item {

    public ItemProjectile(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public abstract String getEntityType();

    public abstract float getThrowForce();

    public boolean canThrow(Player player) {
        return true;
    }

    public void onThrown(Player player, EntityProjectile projectile) {

    }

    public boolean onClickAir(Player player, Vector3 directionVector) {
        Vector3 motion = this.correctMotion(player, directionVector.multiply(this.getThrowForce()));

        CompoundTag nbt = this.correctNBT(new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", motion.x))
                        .add(new DoubleTag("", motion.y))
                        .add(new DoubleTag("", motion.z)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) player.yaw))
                        .add(new FloatTag("", (float) player.pitch))));

        if (!this.canThrow(player)) {
            return false;
        }

        Entity entity = Entity.createEntity(this.getEntityType(), player.getLevel().getChunk(player.getChunkX(), player.getChunkZ()), nbt, player);
        if (entity instanceof EntityProjectile projectile) {
            this.onThrown(player, projectile);

            ProjectileLaunchEvent event = new ProjectileLaunchEvent(projectile);
            event.call();

            if (event.isCancelled()) {
                projectile.close();
            } else {
                if (!player.isCreative()) {
                    this.count--;
                }
                projectile.spawnToAll();
            }
        }

        return true;
    }

    protected Vector3 correctMotion(Player player, Vector3 motion) {
        return motion;
    }

    protected CompoundTag correctNBT(CompoundTag nbt) {
        return nbt;
    }
}
