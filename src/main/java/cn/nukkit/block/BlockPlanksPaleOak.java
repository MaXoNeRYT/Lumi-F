package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;

public class BlockPlanksPaleOak extends BlockSolid {

    public BlockPlanksPaleOak() {
        // Does nothing
    }

    @Override
    public int getId() {
        return PALE_OAK_PLANKS;
    }

    @Override
    public String getName() {
        return "Pale Oak Planks";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }
}
