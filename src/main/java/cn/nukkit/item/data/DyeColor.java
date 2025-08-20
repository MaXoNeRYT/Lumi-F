package cn.nukkit.item.data;

import cn.nukkit.block.data.SignColor;
import cn.nukkit.block.data.BlockColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Dye colors enum.
 */
public enum DyeColor {
    BLACK(0, 15, "Black", "Ink Sac", BlockColor.BLACK_BLOCK_COLOR, SignColor.BLACK),
    RED(1, 14, "Red", BlockColor.RED_BLOCK_COLOR, SignColor.RED),
    GREEN(2, 13, "Green", BlockColor.GREEN_BLOCK_COLOR, SignColor.GREEN),
    BROWN(3, 12, "Brown", "Cocoa Beans", BlockColor.BROWN_BLOCK_COLOR, SignColor.BROWN),
    BLUE(4, 11, "Blue", "Lapis Lazuli", BlockColor.BLUE_BLOCK_COLOR, SignColor.BLUE),
    PURPLE(5, 10, "Purple", BlockColor.PURPLE_BLOCK_COLOR, SignColor.PURPLE),
    CYAN(6, 9, "Cyan", BlockColor.CYAN_BLOCK_COLOR, SignColor.CYAN),
    LIGHT_GRAY(7, 8, "Light Gray", BlockColor.LIGHT_GRAY_BLOCK_COLOR, SignColor.LIGHT_GRAY),
    GRAY(8, 7, "Gray", BlockColor.GRAY_BLOCK_COLOR, SignColor.GRAY),
    PINK(9, 6, "Pink", BlockColor.PINK_BLOCK_COLOR, SignColor.PINK),
    LIME(10, 5, "Lime", BlockColor.LIME_BLOCK_COLOR, SignColor.LIME),
    YELLOW(11, 4, "Yellow", BlockColor.YELLOW_BLOCK_COLOR, SignColor.YELLOW),
    LIGHT_BLUE(12, 3, "Light Blue", BlockColor.LIGHT_BLUE_BLOCK_COLOR, SignColor.LIGHT_BLUE),
    MAGENTA(13, 2, "Magenta", BlockColor.MAGENTA_BLOCK_COLOR, SignColor.MAGENTA),
    ORANGE(14, 1, "Orange", BlockColor.ORANGE_BLOCK_COLOR, SignColor.ORANGE),
    WHITE(15, 0, "White", "Bone Meal", BlockColor.WHITE_BLOCK_COLOR, SignColor.WHITE);

    private final int dyeColorMeta;
    private final int woolColorMeta;
    private final String colorName;
    private final String dyeName;
    private final BlockColor blockColor;
    private final BlockColor signColor;

    private final static Map<Integer, DyeColor> BY_WOOL_DATA = new HashMap<>();
    private final static Map<Integer, DyeColor> BY_DYE_DATA = new HashMap<>();

    DyeColor(int dyeColorMeta, int woolColorMeta, String colorName, BlockColor blockColor, BlockColor signColor) {
        this(dyeColorMeta, woolColorMeta, colorName, colorName + " Dye", blockColor, signColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, String colorName, String dyeName, BlockColor blockColor, BlockColor signColor) {
        this.dyeColorMeta = dyeColorMeta;
        this.woolColorMeta = woolColorMeta;
        this.colorName = colorName;
        this.blockColor = blockColor;
        this.signColor = signColor;
        this.dyeName = dyeName;
    }

    /**
     * Get as BlockColor.
     *
     * @return BlockColor of the DyeColor
     */
    public BlockColor getBlockColor() {
        return this.blockColor;
    }

    public BlockColor getSignColor() {
        return this.signColor;
    }

    /**
     * Get as dye item meta value.
     *
     * @return dye item meta value of the DyeColor
     */
    public int getDyeData() {
        return this.dyeColorMeta;
    }

    /**
     * Get as wool block meta value.
     *
     * @return wool block meta value of the DyeColor
     */
    public int getWoolData() {
        return this.woolColorMeta;
    }

    /**
     * Get color name.
     *
     * @return color name
     */
    public String getName() {
        return this.colorName;
    }

    /**
     * Get dye name.
     *
     * @return dye name
     */
    public String getDyeName() {
        return this.dyeName;
    }

    /**
     * Get DyeColor by dye item meta value.
     *
     * @param dyeColorMeta dye item meta value
     * @return DyeColor
     */
    public static DyeColor getByDyeData(int dyeColorMeta) {
        return BY_DYE_DATA.getOrDefault(dyeColorMeta, DyeColor.WHITE);
    }

    /**
     * Get DyeColor by wool block meta value.
     *
     * @param woolColorMeta wool block meta value
     * @return DyeColor
     */
    public static DyeColor getByWoolData(int woolColorMeta) {
        return BY_WOOL_DATA.getOrDefault(woolColorMeta, DyeColor.WHITE);
    }

    static {
        for (DyeColor color : values()) {
            BY_WOOL_DATA.put(color.woolColorMeta, color);
            BY_DYE_DATA.put(color.dyeColorMeta, color);
        }
    }
}
