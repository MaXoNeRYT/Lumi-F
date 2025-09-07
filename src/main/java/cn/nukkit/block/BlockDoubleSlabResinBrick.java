package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;

public class BlockDoubleSlabResinBrick extends BlockSolidMeta {

    public BlockDoubleSlabResinBrick() {
        this(0);
    }

    public BlockDoubleSlabResinBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RESIN_BRICK_DOUBLE_SLAB;
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
    public String getName() {
        return "Resin Brick Slab";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(RESIN_BRICK_SLAB));
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(Block.get(RESIN_BRICK_SLAB), 0, 2)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_TERRACOTTA_BLOCK_COLOR;
    }
}