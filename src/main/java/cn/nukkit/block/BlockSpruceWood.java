package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockSpruceWood extends BlockLog {

    @Override
    public int getId() {
        return SPRUCE_WOOD;
    }

    @Override
    public String getName() {
        return "Spruce Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_SPRUCE_WOOD;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
