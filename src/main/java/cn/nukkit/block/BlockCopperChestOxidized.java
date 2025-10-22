package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperChestOxidized extends BlockCopperChestBase {

    @Override
    public String getName() {
        return "Oxidized Copper Chest";
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_CHEST;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return  OxidizationLevel.OXIDIZED;
    }
}
