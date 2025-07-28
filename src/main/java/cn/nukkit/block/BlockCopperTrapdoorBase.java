package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.enums.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockCopperTrapdoorBase extends BlockTrapdoor implements Oxidizable, Waxable {
    public BlockCopperTrapdoorBase() {
        this(0);
    }

    public BlockCopperTrapdoorBase(int meta) {
        super(meta);
    }

    @Override
    public double getResistance() {
        return 3;
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
    public boolean onActivate(@NotNull Item item, Player player) {
        if (player != null) {
            if (player.isSneaking()) {
                if (Waxable.super.onActivate(item, player) || Oxidizable.super.onActivate(item, player)) {
                    return true;
                }
            }
        }
        return super.onActivate(item, player);
    }

    @Override
    public void playOpenSound() {
        this.level.addSound(this, Sound.OPEN_TRAPDOOR_COPPER);
    }

    @Override
    public void playCloseSound() {
        // Copper trapdoor does not have close sound
        this.level.addSound(this, Sound.OPEN_TRAPDOOR_COPPER);
    }

    @Override
    public Block getStateWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return Block.get(getCopperId(isWaxed(), oxidizationLevel));
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel())));
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
            case UNAFFECTED -> waxed ? WAXED_COPPER_TRAPDOOR : COPPER_TRAPDOOR;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_TRAPDOOR : EXPOSED_COPPER_TRAPDOOR;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_TRAPDOOR : WEATHERED_COPPER_TRAPDOOR;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_TRAPDOOR : OXIDIZED_COPPER_TRAPDOOR;
        };
    }
}
