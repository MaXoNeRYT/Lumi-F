package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockJungleLog extends BlockLog {

    public BlockJungleLog() {
        this(0);
    }

    public BlockJungleLog(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return JUNGLE_LOG;
    }

    @Override
    public String getName() {
        return "Jungle Log";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_JUNGLE_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
