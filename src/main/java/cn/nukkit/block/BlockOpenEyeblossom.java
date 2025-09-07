package cn.nukkit.block;

public class BlockOpenEyeblossom extends BlockClosedEyeblossom {

    public BlockOpenEyeblossom() {
        this(0);
    }

    public BlockOpenEyeblossom(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Open Eyeblossom";
    }

    @Override
    public int getId() {
        return OPEN_EYEBLOSSOM;
    }
}
