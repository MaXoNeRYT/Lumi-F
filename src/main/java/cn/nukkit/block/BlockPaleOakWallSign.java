package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNamespaceId;

public class BlockPaleOakWallSign extends BlockWallSign {

    public BlockPaleOakWallSign() {
        this(0);
    }

    public BlockPaleOakWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_WALL_SIGN;
    }

    @Override
    protected int getPostId() {
        return PALE_OAK_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Pale Oak Wall Sign";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.PALE_OAK_SIGN);
    }
}
