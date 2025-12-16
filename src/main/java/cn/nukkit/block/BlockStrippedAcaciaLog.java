package cn.nukkit.block;

public class BlockStrippedAcaciaLog extends BlockLogStripped {

    public BlockStrippedAcaciaLog() {
        this(0);
    }
    
    public BlockStrippedAcaciaLog(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_ACACIA_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Acacia Log";
    }
}
