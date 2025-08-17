package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;

public class ItemDyeLime extends ItemDye {
    public ItemDyeLime() {
        super(LIME_DYE, "Lime Dye");
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
