package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperChestWeatheredWaxed extends BlockCopperChestBase {

    @Override
    public String getName() {
        return "Waxed Weathered Copper Chest";
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_CHEST;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return  OxidizationLevel.WEATHERED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
