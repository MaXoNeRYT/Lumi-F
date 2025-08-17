package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeGray extends ItemDye {
    public ItemDyeGray() {
        super(GRAY_DYE, "Gray Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
