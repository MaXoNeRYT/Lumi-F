package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Sound;

public class BlockFenceGatePaleOak extends BlockFenceGate {

    public BlockFenceGatePaleOak() {
        this(0);
    }

    public BlockFenceGatePaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Pale Oak Fence Gate";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}