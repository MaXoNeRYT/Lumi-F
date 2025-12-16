package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperBarsOxidized extends BlockCopperBarsBase {
    @Override
    public String getName() {
        return "Oxidized Copper Bars";
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_BARS;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}
