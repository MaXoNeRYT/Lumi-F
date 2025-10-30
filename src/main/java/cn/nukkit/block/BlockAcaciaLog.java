package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockAcaciaLog extends BlockLog {

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
