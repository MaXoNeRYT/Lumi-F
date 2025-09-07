package cn.nukkit.block;

public class BlockResinBrickWall extends BlockWall {

    public BlockResinBrickWall() {
        this(0);
    }

    public BlockResinBrickWall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RESIN_BRICK_WALL;
    }

    @Override
    public String getName() {
        return "Resin Brick Wall";
    }
}