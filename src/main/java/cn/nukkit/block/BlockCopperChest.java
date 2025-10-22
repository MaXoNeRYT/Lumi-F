package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperChest extends BlockCopperChestBase {

    @Override
    public String getName() {
        return "Copper Chest";
    }

    @Override
    public int getId() {
        return COPPER_CHEST;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return  OxidizationLevel.UNAFFECTED;
    }
}
