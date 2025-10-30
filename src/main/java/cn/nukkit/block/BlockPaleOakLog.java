package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockPaleOakLog extends BlockLog {

    public BlockPaleOakLog() {
        this(0);
    }

    public BlockPaleOakLog(int meta) {
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
    public int getStrippedId() {
        return STRIPPED_PALE_OAK_LOG;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}