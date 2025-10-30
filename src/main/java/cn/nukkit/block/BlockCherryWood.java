package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockCherryWood extends BlockLog {

    public BlockCherryWood() {
        super(0);
    }

    public BlockCherryWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_WOOD;
    }

    @Override
    public String getName() {
        return "Cherry Wood";
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_CHERRY_WOOD;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(CHERRY_WOOD));
    }
}