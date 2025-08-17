package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeLightGray extends ItemDye {
    public ItemDyeLightGray() {
        super(LIGHT_GRAY_DYE, "Light Gray Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
