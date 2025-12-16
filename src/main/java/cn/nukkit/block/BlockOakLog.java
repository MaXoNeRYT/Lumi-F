package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockOakLog extends BlockLog {

    public BlockOakLog() {
        this(0);
    }

    public BlockOakLog(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OAK_LOG;
    }

    @Override
    public String getName() {
        return "Oak Log";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_OAK_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
