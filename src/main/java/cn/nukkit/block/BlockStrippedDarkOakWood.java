package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedDarkOakWood extends BlockLogStripped {

    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Dark Oak Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
