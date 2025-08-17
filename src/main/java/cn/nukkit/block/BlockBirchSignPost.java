package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBirchSign;
import cn.nukkit.item.ItemNamespaceId;

public class BlockBirchSignPost extends BlockSignPost {

    public BlockBirchSignPost() {
    }

    public BlockBirchSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return BIRCH_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Birch Sign Post";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.BIRCH_SIGN);
    }

}
