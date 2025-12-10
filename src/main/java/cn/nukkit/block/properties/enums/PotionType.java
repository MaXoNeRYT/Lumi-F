package cn.nukkit.block.properties.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author AllayMC, SocialMoods
 */
@RequiredArgsConstructor
public enum PotionType {
    EMPTY(-1),
    NORMAL(0),
    SPLASH(1),
    LINGERING(2),
    LAVA(0xF19B),
    UNKNOWN(-2);

    public final int potionTypeData;
    public static final Int2ObjectMap<PotionType> BY_DATA;

    static {
        PotionType[] types = values();
        BY_DATA = new Int2ObjectOpenHashMap<>(types.length);
        for (PotionType type : types) {
            BY_DATA.put(type.potionTypeData, type);
        }
    }

    @NotNull
    public static PotionType getByTypeData(int typeData) {
        return BY_DATA.getOrDefault(typeData, PotionType.UNKNOWN);
    }
}