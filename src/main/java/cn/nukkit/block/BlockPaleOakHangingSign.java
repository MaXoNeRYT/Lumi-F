package cn.nukkit.block;

public class BlockPaleOakHangingSign extends BlockHangingSign {
    public BlockPaleOakHangingSign() {
        this(0);
    }

    public BlockPaleOakHangingSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_HANGING_SIGN;
    }

    @Override
    public String getName() {
        return "Pale Oak Hanging Sign";
    }

}
