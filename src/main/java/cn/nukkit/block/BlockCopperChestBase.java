package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.enums.OxidizationLevel;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.impl.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockCopperChestBase extends BlockChest implements Oxidizable, Waxable {
    public BlockCopperChestBase() {
        this(0);
    }

    public BlockCopperChestBase(int meta) {
        super(meta);
    }

    public boolean tryPair() {
        BlockEntityChest chest = null;

        if (!(this.getLevel().getBlockEntity(this) instanceof BlockEntityChest blockEntity)) {
            return false;
        }

        for (BlockFace side : BlockFace.Plane.HORIZONTAL) {
            Block c = this.getSide(side);
            if (c instanceof BlockCopperChestBase && c.getDamage() == this.getDamage()) {
                BlockEntity entity = this.getLevel().getBlockEntity(c);
                if (entity instanceof BlockEntityChest && !((BlockEntityChest) entity).isPaired()) {
                    chest = (BlockEntityChest) entity;
                    break;
                }
            }
        }

        if (chest != null) {
            chest.pairWith(blockEntity);
            blockEntity.pairWith(chest);

            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        Oxidizable.super.onUpdate(type);
        return super.onUpdate(type);
    }

    @Override
    public void onPlayerRightClick(@NotNull Player player, Item item, BlockFace face, Vector3 clickPoint) {
        if (player.isSneaking()) {
            if(!Waxable.super.onActivate(item, player)) Oxidizable.super.onActivate(item, player);
        }
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return super.onActivate(item, player);
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
            case UNAFFECTED -> waxed ? WAXED_COPPER_CHEST : COPPER_CHEST;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_CHEST : EXPOSED_COPPER_CHEST;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_CHEST : WEATHERED_COPPER_CHEST;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_CHEST : OXIDIZED_COPPER_CHEST;
        };
    }
}