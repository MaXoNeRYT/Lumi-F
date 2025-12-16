package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedJungleWood extends BlockLogStripped {

    public BlockStrippedJungleWood() {
        this(0);
    }

    public BlockStrippedJungleWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_JUNGLE_WOOD;
    }

    @Override
    public String getName() {
        return "Jungle Wood";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
