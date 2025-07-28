package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * Created on 2015/12/25 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class ItemExpBottle extends ItemProjectile {

    public ItemExpBottle() {
        this(0, 1);
    }

    public ItemExpBottle(Integer meta) {
        this(meta, 1);
    }

    public ItemExpBottle(Integer meta, int count) {
        super(EXPERIENCE_BOTTLE, meta, count, "Bottle o' Enchanting");
    }

    @Override
    public String getEntityType() {
        return "ThrownExpBottle";
    }

    @Override
    public float getThrowForce() {
        return 1f;
    }

    @Override
    public void onThrown(Player player, EntityProjectile projectile) {
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
    }
}
