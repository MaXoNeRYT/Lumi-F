package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNamespaceId;

public class BlockPaleOakSignPost extends BlockSignPost {
    public BlockPaleOakSignPost() {

    }

    public BlockPaleOakSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return PALE_OAK_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Pale Oak Sign Post";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.PALE_OAK_SIGN);
    }
}
