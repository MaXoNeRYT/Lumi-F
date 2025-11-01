package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockDarkOakLog extends BlockLog {

    public BlockDarkOakLog() {
        this(0);
    }

    public BlockDarkOakLog(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARK_OAK_LOG;
    }

    @Override
    public String getName() {
        return "Dark Oak Log";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_DARK_OAK_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
