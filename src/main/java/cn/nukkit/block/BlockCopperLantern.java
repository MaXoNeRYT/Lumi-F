package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperLantern extends BlockCopperLanternBase {

    public BlockCopperLantern() {
        this(0);
    }

    public BlockCopperLantern(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COPPER_LANTERN;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
