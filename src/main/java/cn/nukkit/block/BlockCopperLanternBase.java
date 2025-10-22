package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.enums.OxidizationLevel;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockCopperLanternBase extends BlockLantern implements Oxidizable, Waxable {
    public BlockCopperLanternBase() {
        this(0);
    }

    public BlockCopperLanternBase(int meta) {
        super(meta);
    }

    @Override
    public int onUpdate(int type) {
        Oxidizable.super.onUpdate(type);
        return super.onUpdate(type);
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
    public Block getStateWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return Block.get((getCopperId(isWaxed(), oxidizationLevel)), this.getDamage());
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel), this.getDamage()));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel()), this.getDamage()));
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    protected int getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_COPPER_LANTERN : COPPER_LANTERN;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_LANTERN : EXPOSED_COPPER_LANTERN;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_LANTERN : WEATHERED_COPPER_LANTERN;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_LANTERN : OXIDIZED_COPPER_LANTERN;
        };
    }
}