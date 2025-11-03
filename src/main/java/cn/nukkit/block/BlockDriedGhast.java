package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.data.Faceable;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockDriedGhast extends BlockTransparentMeta implements Faceable {
    private static final int CARDINAL_DIRECTION_MASK = 0b0000_0000_0000_0011;
    private static final int REHYDRATION_LEVEL = 0b0000_0000_0000_1100;

    public BlockDriedGhast() {
        this(0);
    }

    public BlockDriedGhast(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dried Ghast";
    }

    @Override
    public int getId() {
        return DRIED_GHAST;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setBlockFace(player.getHorizontalFacing().getOpposite());
        this.setRehydrationLevel(0);
        return this.getLevel().setBlock(this, this, true);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    private void setProperty(int mask, int value) {
        int data = getDamage();
        int shift = Integer.numberOfTrailingZeros(mask);
        int maxValue = (mask >>> shift);
        int clampedValue = value & maxValue;
        setDamage((data & ~mask) | (clampedValue << shift));
    }

    private int getProperty(int mask) {
        int data = getDamage();
        int shift = Integer.numberOfTrailingZeros(mask);
        return (data & mask) >>> shift;
    }

    public void setRehydrationLevel(int level) {
        setProperty(REHYDRATION_LEVEL, level);
    }

    public int getRehydrationLevel() {
        return getProperty(REHYDRATION_LEVEL);
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setProperty(CARDINAL_DIRECTION_MASK, face.getHorizontalIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getProperty(CARDINAL_DIRECTION_MASK));
    }
}
