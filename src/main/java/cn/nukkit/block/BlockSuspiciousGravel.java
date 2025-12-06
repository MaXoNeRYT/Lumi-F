package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.level.Sound;

public class BlockSuspiciousGravel extends BlockSuspicious {

    public BlockSuspiciousGravel() {
        this(0);
    }

    protected BlockSuspiciousGravel(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return Block.SUSPICIOUS_GRAVEL;
    }

    @Override
    public String getName() {
        return "Suspicious Gravel";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public Block getDefaultBlock() {
        return Block.get(BlockID.GRAVEL);
    }

    @Override
    public void playBrushSound() {
        getLevel().addSound(this, Sound.BRUSH_SUSPICIOUS_GRAVEL);
    }

    @Override
    public void playCompletedSound() {
        getLevel().addSound(this, Sound.BRUSH_COMPLETED_SUSPICIOUS_GRAVEL);
    }
}