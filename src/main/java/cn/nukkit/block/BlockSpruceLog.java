package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockSpruceLog extends BlockLog {

    @Override
    public int getId() {
        return SPRUCE_LOG;
    }

    @Override
    public String getName() {
        return "Spruce Log";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_SPRUCE_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
