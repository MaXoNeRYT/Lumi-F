package cn.nukkit.item;

import cn.nukkit.item.data.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemDye extends StringItemBase {

    public ItemDye(@NotNull String namespaceId, @Nullable String name) {
        super(namespaceId, name);
    }

    public abstract DyeColor getDyeColor();

    public static ItemDye getByColor(DyeColor dyeColor) {
        return switch (dyeColor) {
            case BLACK -> new ItemDyeBlack();
            case RED -> new ItemDyeRed();
            case GREEN -> new ItemDyeGreen();
            case BROWN -> new ItemDyeBrown();
            case BLUE -> new ItemDyeBlue();
            case PURPLE -> new ItemDyePurple();
            case CYAN -> new ItemDyeCyan();
            case LIGHT_GRAY -> new ItemDyeLightGray();
            case GRAY -> new ItemDyeGray();
            case PINK -> new ItemDyePink();
            case LIME -> new ItemDyeLime();
            case YELLOW -> new ItemDyeYellow();
            case LIGHT_BLUE -> new ItemDyeLightBlue();
            case MAGENTA -> new ItemDyeMagenta();
            case ORANGE -> new ItemDyeOrange();
            case WHITE -> new ItemDyeWhite();
        };
    }
}
