package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.block.data.BlockColor;
import cn.nukkit.item.ItemNamespaceId;

public class BlockCherryWallSign extends BlockWallSign {

    public BlockCherryWallSign() {
        this(0);
    }

    public BlockCherryWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_WALL_SIGN;
    }

    @Override
    protected int getPostId() {
        return CHERRY_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Cherry Wall Sign";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemNamespaceId.CHERRY_SIGN);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CHERRY_BLOCK_COLOR;
    }
}
