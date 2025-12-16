package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperLanternExposed extends BlockCopperLanternBase {

    public BlockCopperLanternExposed() {
        this(0);
    }

    public BlockCopperLanternExposed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_LANTERN;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}
