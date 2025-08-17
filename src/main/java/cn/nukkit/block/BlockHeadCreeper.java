package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockHeadCreeper extends BlockSkull {

    public BlockHeadCreeper() {
        this(0);
    }

    public BlockHeadCreeper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CREEPER_HEAD;
    }

    @Override
    public String getName() {
        return "Creeper Head";
    }

    @Override
    public SkullType getSkullType() {
        return SkullType.CREEPER;
    }
}