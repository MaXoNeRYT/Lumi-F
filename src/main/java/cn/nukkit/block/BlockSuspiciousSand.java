package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.level.Sound;

public class BlockSuspiciousSand extends BlockSuspicious {

    public BlockSuspiciousSand() {
        this(0);
    }

    protected BlockSuspiciousSand(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return Block.SUSPICIOUS_SAND;
    }

    @Override
    public String getName() {
        return "Suspicious Sand";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 1.25;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public Block getDefaultBlock() {
        return Block.get(BlockID.SAND);
    }

    @Override
    public void playBrushSound() {
        getLevel().addSound(this, Sound.BRUSH_SUSPICIOUS_SAND);
    }

    @Override
    public void playCompletedSound() {
        getLevel().addSound(this, Sound.BRUSH_COMPLETED_SUSPICIOUS_SAND);
    }
}