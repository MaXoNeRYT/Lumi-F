package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockStrippedOakWood extends BlockLogStripped {

    public BlockStrippedOakWood() {
        this(0);
    }

    public BlockStrippedOakWood(int meta) {
        super(meta);
    }

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
