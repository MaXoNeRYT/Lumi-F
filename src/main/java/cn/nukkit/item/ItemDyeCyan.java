package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeCyan extends ItemDye {
    public ItemDyeCyan() {
        super(CYAN_DYE, "Cyan Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
