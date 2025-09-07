package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemNamespaceId;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.Nullable;

public class BlockPaleHangingMoss extends BlockTransparentMeta {

    public BlockPaleHangingMoss() {
        this(0);
    }

    public BlockPaleHangingMoss(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_HANGING_MOSS;
    }

    @Override
    public String getName() {
        return "Pale Hanging Moss";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if(block.up().isSolid() || block.up().getId() == PALE_HANGING_MOSS) {
            if(block.up().getId() == PALE_HANGING_MOSS) {
                BlockPaleHangingMoss blockUp = (BlockPaleHangingMoss) block.up();
                blockUp.setTip(false);
                this.getLevel().setBlock(blockUp, blockUp, true, false);
            }
            this.setTip(true);
            this.getLevel().setBlock(this, this, true, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if(item.getIdentifier().equals(ItemNamespaceId.BONE_MEAL)) {
            this.level.addParticle(new BoneMealParticle(this));
            return tryToGrow();
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.isSupportInvalid()) {
                this.level.scheduleUpdate(this, 0);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.breakUnsupportedChain();
        }

        return 0;
    }

    public boolean isSupportInvalid() {
        Block up = this.up();
        return !up.isSolid() && up.getId() != BlockID.PALE_HANGING_MOSS;
    }

    private void breakUnsupportedChain() {
        Block current = this;

        while (current instanceof BlockPaleHangingMoss) {
            Block blockAbove = current.up();

            if (blockAbove.isSolid() || blockAbove.getId() == BlockID.PALE_HANGING_MOSS) {
                break;
            }

            current = current.up();

            if (!(current instanceof BlockPaleHangingMoss)) {
                current = current.down();
                break;
            }
        }

        Block topUnsupported = current;

        while (current instanceof BlockPaleHangingMoss) {
            Block next = current.down();

            this.level.useBreakOn(this, null, null, true);

            current = next;

            if (!(current instanceof BlockPaleHangingMoss)) {
                break;
            }
        }

        if (topUnsupported.down() instanceof BlockPaleHangingMoss) {
            BlockPaleHangingMoss newBottomMoss = (BlockPaleHangingMoss) topUnsupported.down();
            newBottomMoss.setTip(true);
            this.level.setBlock(newBottomMoss, newBottomMoss, true, false);
        }
    }

    public boolean tryToGrow() {
        Block current = this;

        while (current.down() instanceof BlockPaleHangingMoss) {
            current = current.down();
        }

        Block blockBelow = current.down();

        if (blockBelow.isAir()) {
            if (current instanceof BlockPaleHangingMoss) {
                ((BlockPaleHangingMoss) current).setTip(false);
                this.getLevel().setBlock(current, current, true, false);
            }

            BlockPaleHangingMoss newMoss = new BlockPaleHangingMoss();
            newMoss.setTip(true);
            this.getLevel().setBlock(blockBelow, newMoss, true, false);

            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(@Nullable Player player, Item item) {
        if(item == null || !item.isShears()) return new Item[]{Item.AIR_ITEM};
        return new Item[]{toItem()};
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0, 1);
    }

    public void setTip(boolean tip) {
        this.setDamage(tip ? 1 : 0);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
