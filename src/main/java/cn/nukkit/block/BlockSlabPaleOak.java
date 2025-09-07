package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockSlabPaleOak extends BlockSlab {

    public BlockSlabPaleOak() {
        this(0);
    }

    public BlockSlabPaleOak(int meta) {
        super(meta, PALE_OAK_DOUBLE_SLAB);
    }

    protected BlockSlabPaleOak(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return PALE_OAK_SLAB;
    }

    @Override
    public String getName() {
        return "Pale Oak Slab";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}