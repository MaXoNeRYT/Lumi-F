package cn.nukkit.block;

public class BlockStrippedPaleOakWood extends BlockLogStripped {

    public BlockStrippedPaleOakWood() {
        this(0);
    }

    public BlockStrippedPaleOakWood(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Stripped Pale Oak Wood";
    }

    @Override
    public int getId() {
        return STRIPPED_PALE_OAK_WOOD;
    }
}