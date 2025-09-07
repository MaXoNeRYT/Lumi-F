package cn.nukkit.block;

public class BlockLogStrippedPaleOak extends BlockWoodStripped {

    public BlockLogStrippedPaleOak() {
        this(0);
    }

    public BlockLogStrippedPaleOak(int meta) {
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