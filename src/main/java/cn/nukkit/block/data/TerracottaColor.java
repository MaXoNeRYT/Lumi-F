package cn.nukkit.block.data;

import cn.nukkit.item.data.DyeColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Terracotta color enum
 */
public enum TerracottaColor {
    BLACK(0, DyeColor.BLACK, BlockColor.BLACK_TERRACOTTA_BLOCK_COLOR),
    RED(1, DyeColor.RED, BlockColor.RED_TERRACOTTA_BLOCK_COLOR),
    GREEN(2, DyeColor.GREEN, BlockColor.GREEN_TERRACOTTA_BLOCK_COLOR),
    BROWN(3, DyeColor.BROWN, BlockColor.BROWN_TERRACOTTA_BLOCK_COLOR),
    BLUE(4, DyeColor.BLUE, BlockColor.BLUE_TERRACOTTA_BLOCK_COLOR),
    PURPLE(5, DyeColor.PURPLE, BlockColor.PURPLE_TERRACOTTA_BLOCK_COLOR),
    CYAN(6, DyeColor.CYAN, BlockColor.CYAN_TERRACOTTA_BLOCK_COLOR),
    LIGHT_GRAY(7, DyeColor.LIGHT_GRAY, BlockColor.LIGHT_GRAY_TERRACOTTA_BLOCK_COLOR),
    GRAY(8, DyeColor.GRAY, BlockColor.GRAY_TERRACOTTA_BLOCK_COLOR),
    PINK(9, DyeColor.PINK, BlockColor.PINK_TERRACOTTA_BLOCK_COLOR),
    LIME(10, DyeColor.LIME, BlockColor.LIME_TERRACOTTA_BLOCK_COLOR),
    YELLOW(11, DyeColor.YELLOW, BlockColor.YELLOW_TERRACOTTA_BLOCK_COLOR),
    LIGHT_BLUE(12, DyeColor.LIGHT_BLUE, BlockColor.LIGHT_BLUE_TERRACOTTA_BLOCK_COLOR),
    MAGENTA(13, DyeColor.MAGENTA, BlockColor.MAGENTA_TERRACOTTA_BLOCK_COLOR),
    ORANGE(14, DyeColor.ORANGE, BlockColor.ORANGE_TERRACOTTA_BLOCK_COLOR),
    WHITE(15, DyeColor.WHITE, BlockColor.WHITE_TERRACOTTA_BLOCK_COLOR);

    private final int terracottaData;
    private final DyeColor dyeColor;
    private final BlockColor blockColor;

    private final static Map<Integer, TerracottaColor> BY_TERRACOTTA_DATA = new HashMap<>();
    private final static Map<DyeColor, TerracottaColor> BY_DYE_DATA = new HashMap<>();

    TerracottaColor(int terracottaData, DyeColor dyeColor, BlockColor blockColor) {
        this.terracottaData = terracottaData;
        this.dyeColor = dyeColor;
        this.blockColor = blockColor;
    }

    /**
     * Get as terracotta block meta value
     *
     * @return terracotta block meta value of the TerracottaColor
     */
    public int getTerracottaData() {
        return this.terracottaData;
    }

    /**
     * Get dye color.
     *
     * @return DyeColor of the TerracottaColor
     */
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    /**
     * Get as BlockColor.
     *
     * @return BlockColor of the TerracottaColor
     */
    public BlockColor getBlockColor() {
        return this.blockColor;
    }

    /**
     * Get TerracottaColor by dye item meta value
     *
     * @param dyeColor Dye color
     * @return TerracottaColor
     */
    public static TerracottaColor getByDyeData(DyeColor dyeColor) {
        return BY_DYE_DATA.getOrDefault(dyeColor, WHITE);
    }

    /**
     * Get TerracottaColor by terracotta block meta value
     *
     * @param terracottaColorMeta terracotta block meta value
     * @return TerracottaColor
     */
    public static TerracottaColor getByTerracottaData(int terracottaColorMeta) {
        return BY_TERRACOTTA_DATA.getOrDefault(terracottaColorMeta, WHITE);
    }

    static {
        for (TerracottaColor color : values()) {
            BY_TERRACOTTA_DATA.put(color.terracottaData, color);
            BY_DYE_DATA.put(color.dyeColor, color);
        }
    }
}
