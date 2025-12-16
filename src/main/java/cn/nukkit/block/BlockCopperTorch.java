package cn.nukkit.block;

public class BlockCopperTorch extends BlockTorch {

    public BlockCopperTorch() {
        this(0);
    }

    public BlockCopperTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Copper Torch";
    }

    @Override
    public int getId() {
        return COPPER_TORCH;
    }
}
