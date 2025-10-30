package cn.nukkit.block;

public class BlockStrippedBirchLog extends BlockLogStripped {

    public BlockStrippedBirchLog() {
        this(0);
    }
    
    public BlockStrippedBirchLog(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_BIRCH_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Birch Log";
    }
}
