package cn.nukkit.block;

public class BlockStrippedOakLog extends BlockLogStripped {

    public BlockStrippedOakLog() {
        this(0);
    }
    
    public BlockStrippedOakLog(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_OAK_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Oak Log";
    }
}
