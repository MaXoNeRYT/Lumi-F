package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.impl.BlockEntityBrushableBlock;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNamespaceId;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockSuspicious extends BlockFallableMeta implements BlockEntityHolder<BlockEntityBrushableBlock> {
    public BlockSuspicious() {
        this(0);
    }

    protected BlockSuspicious(int meta) {
        super(meta);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.AIR_ITEM};
    }

    @NotNull
    @Override
    public Class<? extends BlockEntityBrushableBlock> getBlockEntityClass() {
        return BlockEntityBrushableBlock.class;
    }

    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.BRUSHABLE_BLOCK;
    }

    public abstract Block getDefaultBlock();

    public abstract void playBrushSound();

    public abstract void playCompletedSound();

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockEntityBrushableBlock blockEntity = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, BlockEntity.getDefaultCompound(block, getBlockEntityType()));
        return blockEntity != null;
    }

    @Override
    public int onTouch(@NotNull Vector3 vector, @NotNull Item item, @NotNull BlockFace face, float fx, float fy, float fz, @Nullable Player player, PlayerInteractEvent.Action action) {
        BlockEntityBrushableBlock blockEntity = this.getBlockEntity();

        if (action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && item.getNamespaceId().equals(ItemNamespaceId.BRUSH)) {
            int state = blockEntity.getCompletionState();

            BlockFace brushDirection = blockEntity.getBrushDirection();

            if (brushDirection == BlockFace.DOWN) {
                blockEntity.setBrushDirection(face);
                brushDirection = face;
            }

            if (face == brushDirection) {
                if (blockEntity.getBrushCount() != BlockEntityBrushableBlock.REQUIRED_BRUSHES_TO_BREAK) {
                    blockEntity.setBrushCount(blockEntity.getBrushCount() + 1);
                }

                if (blockEntity.getBrushCount() >= BlockEntityBrushableBlock.REQUIRED_BRUSHES_TO_BREAK) {
                    getLevel().dropItem(this.getSide(face).add(0.5, 0, 0.5), blockEntity.getItem(), Vector3.ZERO, true, 10);
                    getLevel().setBlock(this, getDefaultBlock());
                    playBrushSound();
                    return 1;
                }

                if (getDamage() != state) {
                    this.setDamage(state);
                    playBrushSound();
                    getLevel().setBlock(this, this);
                }
            }
        }
        return 1;
    }
}
