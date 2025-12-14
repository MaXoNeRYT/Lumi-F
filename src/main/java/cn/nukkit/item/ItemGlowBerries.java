package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.network.protocol.ProtocolInfo;

public class ItemGlowBerries extends ItemFood {

    public ItemGlowBerries() {
        this(0, 1);
    }

    public ItemGlowBerries(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowBerries(Integer meta, int count) {
        super(GLOW_BERRIES, 0, count, "Glow Berries");
        this.block = Block.get(CAVE_VINES);
    }

    @Override
    public int getFoodRestore() {
        return 2;
    }

    @Override
    public float getSaturationRestore() {
        return 0.4F;
    }
}
