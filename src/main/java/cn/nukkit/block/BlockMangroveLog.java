package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.block.data.BlockColor;

public class BlockMangroveLog extends BlockLog {

    public BlockMangroveLog() {
        this(0);
    }

    public BlockMangroveLog(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Log";
    }

    @Override
    public int getId() {
        return MANGROVE_LOG;
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_MANGROVE_LOG;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}