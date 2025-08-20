package cn.nukkit.block;

import cn.nukkit.level.Sound;
import cn.nukkit.block.data.BlockColor;

public class BlockTrapdoorCherry extends BlockTrapdoor {
    public BlockTrapdoorCherry() {
        this(0);
    }

    public BlockTrapdoorCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Cherry Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CHERRY_BLOCK_COLOR;
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_CHERRY_WOOD_TRAPDOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_CHERRY_WOOD_TRAPDOOR);
    }
}
