package cn.nukkit.block;

public class BlockStrippedJungleLog extends BlockLogStripped {

    public BlockStrippedJungleLog() {
        this(0);
    }
    
    public BlockStrippedJungleLog(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_JUNGLE_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Jungle Log";
    }
}
