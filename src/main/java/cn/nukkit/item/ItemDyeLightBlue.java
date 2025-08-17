package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeLightBlue extends ItemDye {
    public ItemDyeLightBlue() {
        super(LIGHT_BLUE_DYE, "Light Blue Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
