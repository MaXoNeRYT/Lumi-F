package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemEnderPearl extends ItemProjectile {

    public ItemEnderPearl() {
        this(0, 1);
    }

    public ItemEnderPearl(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderPearl(Integer meta, int count) {
        super(ENDER_PEARL, 0, count, "Ender Pearl");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public String getEntityType() {
        return "EnderPearl";
    }

    @Override
    public boolean canThrow(Player player) {
        return player.getServer().getTick() - player.getLastEnderPearlThrowingTick() >= 20;
    }

    @Override
    public void onThrown(Player player, EntityProjectile projectile) {
        player.setLastEnderPearlThrowingTick();
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
