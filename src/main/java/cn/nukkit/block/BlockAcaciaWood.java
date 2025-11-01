package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockAcaciaWood extends BlockLog {

    public BlockAcaciaWood() {
        this(0);
    }

    public BlockAcaciaWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_WOOD;
    }

    @Override
    public String getName() {
        return "Acacia Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_ACACIA_WOOD;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
