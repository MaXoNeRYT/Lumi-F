package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.AnimatePacket;

public abstract class BlockLog extends BlockSolidMeta {

    public static final short[] faces = new short[]{
            0, //y
            0,
            2, //z
            2,
            1,
            1 //x
    };

    public BlockLog() {
        this(0);
    }

    public BlockLog(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 10;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(faces[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public abstract int getStrippedId();

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isAxe()) {
            Block strippedBlock = Block.get(this.getStrippedId(), this.getDamage());
            item.useOn(this);

            AnimatePacket packet = new AnimatePacket();
            packet.eid = player.getId();
            packet.action = AnimatePacket.Action.SWING_ARM;
            player.dataPacket(packet);
            
            this.level.setBlock(this, strippedBlock, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
