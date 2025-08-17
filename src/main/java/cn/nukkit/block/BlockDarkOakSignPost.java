package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDarkOakSign;
import cn.nukkit.item.ItemNamespaceId;

public class BlockDarkOakSignPost extends BlockSignPost {

    public BlockDarkOakSignPost() {
    }

    public BlockDarkOakSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARKOAK_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return DARKOAK_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Dark Oak Sign Post";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.DARK_OAK_SIGN);
    }
}
