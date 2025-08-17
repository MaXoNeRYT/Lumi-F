package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyePurple extends ItemDye {
    public ItemDyePurple() {
        super(PURPLE_DYE, "Purple Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
