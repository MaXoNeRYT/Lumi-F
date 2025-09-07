package cn.nukkit.block;

public class BlockClosedEyeblossom extends BlockFlower {

    public BlockClosedEyeblossom() {
        this(0);
    }

    public BlockClosedEyeblossom(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Closed Eyeblossom";
    }

    @Override
    public int getId() {
        return CLOSED_EYEBLOSSOM;
    }
}
