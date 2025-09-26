package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockVault extends BlockSolidMeta {
    public BlockVault() {
        this(0);
    }

    public BlockVault(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Vault";
    }

    @Override
    public int getId() {
        return VAULT;
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 50;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
