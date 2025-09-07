package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class BlockLichen extends BlockTransparentMeta {
    public static final NukkitRandom RANDOM = new NukkitRandom();

    public BlockLichen() {
        this(0);
    }

    public BlockLichen(int meta) {
        super(meta);
    }

    public BlockFace[] getGrowthSides() {
        Stream<BlockFace> returns = Arrays.stream(BlockFace.values()).filter(this::isGrowthToSide);
        return returns.toArray(BlockFace[]::new);
    }

    public void witherAtSide(BlockFace side) {
        if (isGrowthToSide(side)) {
            this.setDamage(this.getDamage() ^ (0b000001 << side.getDUSWNEIndex()));
            getLevel().setBlock(this, this, true, true);
        }
    }

    public boolean isGrowthToSide(BlockFace side) {
        return (this.getDamage() >> side.getDUSWNEIndex() & 0x1) > 0;
    }

    public void growToSide(BlockFace side) {
        if (!isGrowthToSide(side)) {
            this.setDamage(this.getDamage() | (0b000001 << side.getDUSWNEIndex()));
            getLevel().setBlock(this, this, true, true);
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.isSolid() && target instanceof BlockLichen) {
            return false;
        }

        if(block.getSide(face).isSolid(face)) {
            growToSide(face);
            return true;
        }

        getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        for (BlockFace side : BlockFace.values()) {
            final Block support = this.getSide(side);
            if (isGrowthToSide(side) && support != null && !support.isSolid()) {
                //this.witherAtSide(side);
            }
        }
        return super.onUpdate(type);
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvest(Item item) {
        return item.isAxe() || item.isShears();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }
}
