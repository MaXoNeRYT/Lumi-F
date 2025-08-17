package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeOrange extends ItemDye {
    public ItemDyeOrange() {
        super(ORANGE_DYE, "Orange Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
