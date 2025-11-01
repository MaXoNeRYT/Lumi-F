package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedBirchWood extends BlockLogStripped {

    public BlockStrippedBirchWood() {
        this(0);
    }

    public BlockStrippedBirchWood(int meta) {
        super(meta);
    }

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
