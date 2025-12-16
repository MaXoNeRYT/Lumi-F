package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemCocoaBeans extends StringItemBase {

    public ItemCocoaBeans() {
        super(COCOA_BEANS, "Cocoa Beans");
        this.block = Block.get(BlockID.COCOA);
    }
}
