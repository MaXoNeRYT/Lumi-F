package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNamespaceId;
import cn.nukkit.level.Sound;
import cn.nukkit.block.data.BlockColor;

public class BlockDoorCherry extends BlockDoorWood {

    public BlockDoorCherry() {
        this(0);
    }

    public BlockDoorCherry(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Door Block";
    }

    @Override
    public int getId() {
        return CHERRY_DOOR;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.CHERRY_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CHERRY_BLOCK_COLOR;
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_CHERRY_WOOD_DOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_CHERRY_WOOD_DOOR);
    }
}