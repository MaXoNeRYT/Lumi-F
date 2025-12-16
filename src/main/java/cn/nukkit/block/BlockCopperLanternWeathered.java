package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperLanternWeathered extends BlockCopperLanternBase {

    public BlockCopperLanternWeathered() {
        this(0);
    }

    public BlockCopperLanternWeathered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_LANTERN;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
