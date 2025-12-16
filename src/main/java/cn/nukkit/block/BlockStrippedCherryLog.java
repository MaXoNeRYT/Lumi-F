package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedCherryLog extends BlockLogStripped {

    public BlockStrippedCherryLog() {
        super(0);
    }

    public BlockStrippedCherryLog(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CHERRY_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Cherry Log";
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTTA_BLOCK_COLOR;
    }
}