package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperBarsExposed extends BlockCopperBarsBase {
    @Override
    public String getName() {
        return "Exposed Copper Bars";
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_BARS;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}
