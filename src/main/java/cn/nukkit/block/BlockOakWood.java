package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockOakWood extends BlockLog {

    public BlockOakWood() {
        this(0);
    }

    public BlockOakWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OAK_WOOD;
    }

    @Override
    public String getName() {
        return "Oak Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_OAK_WOOD;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
