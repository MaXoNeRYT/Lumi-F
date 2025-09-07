package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockLogPaleOak extends BlockWood {

    public BlockLogPaleOak() {
        this(0);
    }

    public BlockLogPaleOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pale Oak Log";
    }

    @Override
    public int getId() {
        return PALE_OAK_LOG;
    }

    @Override
    protected int getStrippedId() {
        return STRIPPED_PALE_OAK_LOG;
    }

    @Override
    protected int getStrippedDamage() {
        return this.getDamage() & ~0x3;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}