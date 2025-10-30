package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedOakWood extends BlockLogStripped {

    @Override
    public int getId() {
        return STRIPPED_OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Oak Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
