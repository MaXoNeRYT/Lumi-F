package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeMagenta extends ItemDye {
    public ItemDyeMagenta() {
        super(MAGENTA_DYE, "Magenta Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
