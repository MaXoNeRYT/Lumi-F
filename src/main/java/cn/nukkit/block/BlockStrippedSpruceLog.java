package cn.nukkit.block;

public class BlockStrippedSpruceLog extends BlockLogStripped {

    public BlockStrippedSpruceLog() {
        this(0);
    }
    
    public BlockStrippedSpruceLog(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_SPRUCE_LOG;
    }
    
    @Override
    public String getName() {
        return "Stripped Spruce Log";
    }
}
