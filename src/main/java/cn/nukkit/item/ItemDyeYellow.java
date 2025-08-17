package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeYellow extends ItemDye {
    public ItemDyeYellow() {
        super(YELLOW_DYE, "Yellow Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
