package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockHeadPiglin extends BlockSkull {

    public BlockHeadPiglin() {
        this(0);
    }

    public BlockHeadPiglin(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PIGLIN_HEAD;
    }

    @Override
    public String getName() {
        return "Piglin Head";
    }

    @Override
    public SkullType getSkullType() {
        return SkullType.PIGLIN;
    }
}