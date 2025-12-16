package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockBirchLog extends BlockLog {

    public BlockBirchLog() {
        this(0);
    }

    public BlockBirchLog(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_LOG;
    }

    @Override
    public String getName() {
        return "Birch Log";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_BIRCH_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
