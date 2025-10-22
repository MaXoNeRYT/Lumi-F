package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperLanternWaxed extends BlockCopperLanternBase {

    public BlockCopperLanternWaxed() {
        this(0);
    }

    public BlockCopperLanternWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_COPPER_LANTERN;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
