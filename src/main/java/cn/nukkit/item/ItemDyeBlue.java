package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeBlue extends ItemDye {
    public ItemDyeBlue() {
        super(BLUE_DYE, "Blue Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
