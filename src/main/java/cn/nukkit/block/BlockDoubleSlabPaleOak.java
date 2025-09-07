package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;

public class BlockDoubleSlabPaleOak extends BlockSolidMeta {

    public BlockDoubleSlabPaleOak() {
        this(0);
    }

    public BlockDoubleSlabPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_DOUBLE_SLAB;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public String getName() {
        return "Double Pale Oak Slab";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(PALE_OAK_SLAB));
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(Block.get(PALE_OAK_SLAB), 0, 2)
        };
    }
}