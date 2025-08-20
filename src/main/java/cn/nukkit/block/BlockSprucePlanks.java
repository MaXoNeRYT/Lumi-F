package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;

public class BlockSprucePlanks extends BlockPlanks {

    public BlockSprucePlanks() {}

    @Override
    public String getName() {
        return "Spruce Planks";
    }

    @Override
    public int getId() {
        return SPRUCE_PLANKS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
