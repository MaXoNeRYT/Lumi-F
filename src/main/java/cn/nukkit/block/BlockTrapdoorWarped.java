package cn.nukkit.block;

import cn.nukkit.level.Sound;
import cn.nukkit.block.data.BlockColor;

public class BlockTrapdoorWarped extends BlockTrapdoor {

    public BlockTrapdoorWarped() {
        this(0);
    }

    public BlockTrapdoorWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Warped Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_NETHER_WOOD_TRAPDOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_NETHER_WOOD_TRAPDOOR);
    }
}
