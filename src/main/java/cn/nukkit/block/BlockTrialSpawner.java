package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockTrialSpawner extends BlockSolidMeta {
    public BlockTrialSpawner() {
        this(0);
    }

    public BlockTrialSpawner(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Trial Spawner";
    }

    @Override
    public int getId() {
        return TRIAL_SPAWNER;
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
