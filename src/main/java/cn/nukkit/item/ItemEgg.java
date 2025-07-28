package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityClimateVariant;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemEgg extends ItemProjectile {

    public ItemEgg() {
        this(0, 1);
    }

    public ItemEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemEgg(Integer meta, int count) {
        super(EGG, meta, count, "Egg");
    }

    public ItemEgg(int id, Integer meta, int count) {
        super(id, meta, count, "Egg");
    }

    @Override
    public String getEntityType() {
        return "Egg";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
    
    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public void onThrown(Player player, EntityProjectile projectile) {
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
    }

    @Override
    protected CompoundTag correctNBT(CompoundTag nbt) {
        nbt.putString("variant", EntityClimateVariant.Variant.TEMPERATE.getName());
        return nbt;
    }
}
