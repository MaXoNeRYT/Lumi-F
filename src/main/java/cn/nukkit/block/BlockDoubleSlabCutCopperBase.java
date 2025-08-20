package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.enums.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.block.data.BlockColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockDoubleSlabCutCopperBase extends BlockSolidMeta implements Oxidizable, Waxable {
    public BlockDoubleSlabCutCopperBase(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return Waxable.super.onActivate(item, player)
                || Oxidizable.super.onActivate(item, player);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    @Override
    public Block getStateWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        BlockDoubleSlabCutCopperBase slab = (BlockDoubleSlabCutCopperBase) Block.get((getCopperId(isWaxed(), oxidizationLevel)));
        slab.setDamage(getDamage());
        return slab;
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }

        BlockDoubleSlabCutCopperBase slab = (BlockDoubleSlabCutCopperBase) Block.get((getCopperId(isWaxed(), oxidizationLevel)));
        slab.setDamage(getDamage());

        return getValidLevel().setBlock(this, slab);
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }

        BlockDoubleSlabCutCopperBase slab = (BlockDoubleSlabCutCopperBase) Block.get((getCopperId(waxed, getOxidizationLevel())));
        slab.setDamage(getDamage());

        return getValidLevel().setBlock(this, slab);
    }

    protected int getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed? WAXED_DOUBLE_CUT_COPPER_SLAB : DOUBLE_CUT_COPPER_SLAB;
            case EXPOSED:
                return waxed? WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB : EXPOSED_DOUBLE_CUT_COPPER_SLAB;
            case WEATHERED:
                return waxed? WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB : WEATHERED_DOUBLE_CUT_COPPER_SLAB;
            case OXIDIZED:
                return waxed? WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB : OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
            default:
                return getId();
        }
    }
}
