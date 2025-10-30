package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockJungleWood extends BlockLog {

    @Override
    public int getId() {
        return JUNGLE_WOOD;
    }

    @Override
    public String getName() {
        return "Jungle Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_JUNGLE_WOOD;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
