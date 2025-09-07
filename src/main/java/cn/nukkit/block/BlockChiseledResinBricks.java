package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.item.ItemTool;

public class BlockChiseledResinBricks extends BlockSolid {

    @Override
    public int getId() {
        return CHISELED_RESIN_BRICKS;
    }

    @Override
    public String getName() {
        return "Chiseled Resin Bricks";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTTA_BLOCK_COLOR;
    }
}
