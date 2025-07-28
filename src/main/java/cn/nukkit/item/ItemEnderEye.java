package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemEnderEye extends ItemProjectile {

    public ItemEnderEye() {
        this(0, 1);
    }

    public ItemEnderEye(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderEye(Integer meta, int count) {
        super(ENDER_EYE, meta, count, "Ender Eye");
    }

    @Override
    public String getEntityType() {
        return "EnderEye";
    }

    @Override
    public boolean canThrow(Player player) {
        if (player.getLevel().getDimension() != Level.DIMENSION_OVERWORLD) {
            return false;
        }
        return player.getServer().getTick() - player.getLastEnderPearlThrowingTick() >= 20;
    }

    @Override
    public void onThrown(Player player, EntityProjectile projectile) {
        player.setLastEnderPearlThrowingTick();
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
    }

    @Override
    protected Vector3 correctMotion(Player player, Vector3 motion) {
        Vector3 vector = player.subtract(player).normalize();
        vector.y = 0.55f;
        return vector.divide(this.getThrowForce());
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
