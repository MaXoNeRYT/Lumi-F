package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeRed extends ItemDye {
    public ItemDyeRed() {
        super(RED_DYE, "Red Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
