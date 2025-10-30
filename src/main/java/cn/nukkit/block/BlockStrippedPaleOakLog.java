package cn.nukkit.block;

public class BlockStrippedPaleOakLog extends BlockLogStripped {

    public BlockStrippedPaleOakLog() {
        this(0);
    }

    public BlockStrippedPaleOakLog(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Stripped Pale Oak Log";
    }

    @Override
    public int getId() {
        return STRIPPED_PALE_OAK_LOG;
    }
}