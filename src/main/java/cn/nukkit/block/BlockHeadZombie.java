package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockHeadZombie extends BlockSkull {

    public BlockHeadZombie() {
        this(0);
    }

    public BlockHeadZombie(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ZOMBIE_HEAD;
    }

    @Override
    public String getName() {
        return "Zombie Head";
    }

    @Override
    public SkullType getSkullType() {
        return SkullType.ZOMBIE;
    }
}