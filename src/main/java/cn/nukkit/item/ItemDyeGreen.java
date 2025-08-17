package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeGreen extends ItemDye {
    public ItemDyeGreen() {
        super(GREEN_DYE, "Green Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
