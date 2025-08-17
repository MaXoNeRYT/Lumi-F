package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemNamespaceId;

public class BlockAcaciaWallSign extends BlockWallSign {

    public BlockAcaciaWallSign() {
        this(0);
    }

    public BlockAcaciaWallSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Wall Sign";
    }

    @Override
    public int getId() {
        return ACACIA_WALL_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.ACACIA_SIGN);
    }

    @Override
    protected int getPostId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return ACACIA_WALL_SIGN;
    }
}
