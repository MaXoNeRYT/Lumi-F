package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeWhite extends ItemDye {
    public ItemDyeWhite() {
        super(WHITE_DYE, "White Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
