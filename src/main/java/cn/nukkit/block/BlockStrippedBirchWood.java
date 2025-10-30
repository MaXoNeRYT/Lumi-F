package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedBirchWood extends BlockLogStripped {

    @Override
    public int getId() {
        return STRIPPED_BIRCH_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Birch Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
