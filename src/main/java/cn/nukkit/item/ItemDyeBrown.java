package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeBrown extends ItemDye {
    public ItemDyeBrown() {
        super(BROWN_DYE, "Brown Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
