package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperChestExposed extends BlockCopperChestBase {

    @Override
    public String getName() {
        return "Exposed Copper Chest";
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_CHEST;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return  OxidizationLevel.EXPOSED;
    }
}
