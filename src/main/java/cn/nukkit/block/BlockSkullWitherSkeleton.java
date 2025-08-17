package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockSkullWitherSkeleton extends BlockSkull {

    public BlockSkullWitherSkeleton() {
        this(0);
    }

    public BlockSkullWitherSkeleton(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WITHER_SKELETON_SKULL;
    }

    @Override
    public String getName() {
        return "Wither Skeleton Skull";
    }

    @Override
    public SkullType getSkullType() {
        return SkullType.WITHER_SKELETON;
    }
}