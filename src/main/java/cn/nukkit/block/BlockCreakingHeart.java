package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockCreakingHeart extends BlockSolidMeta {
    public BlockCreakingHeart() {
        this(0);
    }

    public BlockCreakingHeart(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CREAKING_HEART;
    }

    @Override
    public String getName() {
        return "Creaking Heart";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 10;
    }

    @Override
    public double getResistance() {
        return 10;
    }
}
