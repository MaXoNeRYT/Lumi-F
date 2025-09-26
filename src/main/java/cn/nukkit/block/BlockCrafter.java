package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

public class BlockCrafter extends BlockSolidMeta {
    public BlockCrafter() {
        this(0);
    }

    public BlockCrafter(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crafter";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        //TODO: remove this temporary way to make it look better after complete implementation
        this.setDamage(4);
        return this.level.setBlock(this, this);
    }

    @Override
    public int getId() {
        return CRAFTER;
    }
}
