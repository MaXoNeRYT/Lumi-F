package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNamespaceId;

public class BlockDoorPaleOak extends BlockDoorWood {

    public BlockDoorPaleOak() {
        this(0);
    }

    public BlockDoorPaleOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pale Oak Door";
    }

    @Override
    public int getId() {
        return PALE_OAK_DOOR;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.CHERRY_DOOR);
    }
}