package cn.nukkit.block;

public class BlockStrippedDarkOakLog extends BlockLogStripped {

    public BlockStrippedDarkOakLog() {
        this(0);
    }
    
    public BlockStrippedDarkOakLog(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Dark Oak Log";
    }
}
