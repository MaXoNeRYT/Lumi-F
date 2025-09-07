package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockStairsResinBricks extends BlockStairs {
    public BlockStairsResinBricks() {
        this(0);
    }

    public BlockStairsResinBricks(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RESIN_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Resin Brick Stairs";
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
}
