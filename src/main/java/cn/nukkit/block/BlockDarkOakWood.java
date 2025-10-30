package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockDarkOakWood extends BlockLog {

    @Override
    public int getId() {
        return DARK_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Dark Oak Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_DARK_OAK_WOOD;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
