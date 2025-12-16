package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedAcaciaWood extends BlockLogStripped {

    public BlockStrippedAcaciaWood() {
        this(0);
    }

    public BlockStrippedAcaciaWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_ACACIA_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Acacia Wood";
    }
    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
