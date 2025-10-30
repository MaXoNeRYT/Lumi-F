package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedCherryWood extends BlockLogStripped {

    public BlockStrippedCherryWood() {
        super(0);
    }

    public BlockStrippedCherryWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CHERRY_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Cherry Wood";
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTTA_BLOCK_COLOR;
    }
}
