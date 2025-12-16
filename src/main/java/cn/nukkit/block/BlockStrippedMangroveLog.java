package cn.nukkit.block;


import cn.nukkit.block.data.BlockColor;

public class BlockStrippedMangroveLog extends BlockLogStripped {

    public BlockStrippedMangroveLog() {
        this(0);
    }

    public BlockStrippedMangroveLog(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Stripped Mangrove Log";
    }

    @Override
    public int getId() {
        return STRIPPED_MANGROVE_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
