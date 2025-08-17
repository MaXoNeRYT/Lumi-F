package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Justin
 */
public class BlockSkullSkeleton extends BlockSkull implements Faceable, BlockEntityHolder<BlockEntitySkull> {

    public BlockSkullSkeleton() {
        this(0);
    }

    public BlockSkullSkeleton(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SKELETON_SKULL_BLOCK;
    }

    public SkullType getSkullType() {
        return SkullType.SKELETON;
    }
}
