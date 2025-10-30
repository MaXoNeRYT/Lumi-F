package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockMangroveWood extends BlockLog {

    public BlockMangroveWood() {
        super(0);
    }

    public BlockMangroveWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_WOOD;
    }

    @Override
    public String getName() {
        return "Mangrove Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_MANGROVE_WOOD;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
