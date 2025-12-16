package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockBirchWood extends BlockLog {

    public BlockBirchWood() {
        this(0);
    }

    public BlockBirchWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_WOOD;
    }

    @Override
    public String getName() {
        return "Birch Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_BIRCH_WOOD;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
