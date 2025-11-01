package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockSpruceLog extends BlockLog {

    public BlockSpruceLog() {
        this(0);
    }

    public BlockSpruceLog(int meta) {
        super(meta);
    }

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
