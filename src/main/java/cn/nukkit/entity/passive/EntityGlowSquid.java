package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemGlowInkSac;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public class EntityGlowSquid extends EntitySquid {

    public static final int NETWORK_ID = 129;

    public EntityGlowSquid(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public Item[] getDrops() {
        ItemGlowInkSac item = new ItemGlowInkSac();
        item.setCount(Utils.rand(1, 3));
        return new Item[]{item};
    }
}
