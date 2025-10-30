package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockPaleOakWood extends BlockLog {

    public BlockPaleOakWood() {
        super(0);
    }

    public BlockPaleOakWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Pale Oak Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_PALE_OAK_WOOD;
    }
}
