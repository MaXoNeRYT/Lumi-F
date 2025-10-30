package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedSpruceWood extends BlockLogStripped {

    @Override
    public int getId() {
        return STRIPPED_SPRUCE_WOOD;
    }

    @Override
    public String getName() {
        return "Spruce Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
