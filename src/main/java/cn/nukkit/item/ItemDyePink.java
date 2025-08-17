package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyePink extends ItemDye {
    public ItemDyePink() {
        super(PINK_DYE, "Pink Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
