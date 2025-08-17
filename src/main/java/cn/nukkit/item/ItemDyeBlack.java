package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeBlack extends ItemDye {
    public ItemDyeBlack() {
        super(BLACK_DYE, "Black Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
