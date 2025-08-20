package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNamespaceId;
import cn.nukkit.level.Sound;
import cn.nukkit.block.data.BlockColor;

public class BlockDoorBamboo extends BlockDoorWood {

    public BlockDoorBamboo() {
        this(0);
    }

    public BlockDoorBamboo(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Door Block";
    }

    @Override
    public int getId() {
        return BAMBOO_DOOR;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.BAMBOO_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BAMBOO_BLOCK_COLOR;
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_BAMBOO_WOOD_DOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_BAMBOO_WOOD_DOOR);
    }
}