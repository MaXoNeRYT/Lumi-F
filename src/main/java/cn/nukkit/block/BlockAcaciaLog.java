package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockAcaciaLog extends BlockLog {

    public BlockAcaciaLog() {
        this(0);
    }

    public BlockAcaciaLog(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_LOG;
    }

    @Override
    public String getName() {
        return "Acacia Log";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_ACACIA_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
