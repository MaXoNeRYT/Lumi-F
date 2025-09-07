package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockResinClump extends BlockLichen {
    public BlockResinClump() {
        this(0);
    }

    public BlockResinClump(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RESIN_CLUMP;
    }

    @Override
    public String getName() {
        return "Resin Clump";
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTTA_BLOCK_COLOR;
    }
}
