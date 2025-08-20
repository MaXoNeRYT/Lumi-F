package cn.nukkit.block;

import cn.nukkit.block.properties.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsCutCopper extends BlockStairsCutCopperBase {

    public BlockStairsCutCopper() {
        this(0);
    }

    public BlockStairsCutCopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CUT_COPPER_STAIRS;
    }

    @Override
    public String getName() {
        return "Cut Copper Stairs";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
