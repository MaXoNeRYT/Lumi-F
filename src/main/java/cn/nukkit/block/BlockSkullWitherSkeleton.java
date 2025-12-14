package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityWither;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @NotNull Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            EntityWither.trySpawnWither(this);
            return true;
        }
        return false;
    }
}