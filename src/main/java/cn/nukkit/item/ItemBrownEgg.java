package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityClimateVariant;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

public class ItemBrownEgg extends ItemEgg implements StringItem {

    public ItemBrownEgg() {
        super(STRING_IDENTIFIED_ITEM, 0, 1);
    }

    @Override
    public String getNamespaceId() {
        return BROWN_EGG;
    }

    @Override
    public String getNamespaceId(int protocolId) {
        return BROWN_EGG;
    }

    @Override
    public void onThrown(Player player, EntityProjectile projectile) {
        player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
    }

    @Override
    protected CompoundTag correctNBT(CompoundTag nbt) {
        nbt.putString("variant", EntityClimateVariant.Variant.WARM.getName());
        return nbt;
    }

    @Override
    public boolean isSupportedOn(int protocolId) {
        return protocolId >= ProtocolInfo.v1_21_70;
    }
}

