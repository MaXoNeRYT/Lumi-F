package cn.nukkit.block;

import cn.nukkit.level.Sound;
import cn.nukkit.block.data.BlockColor;

public class BlockTrapdoorBamboo extends BlockTrapdoor {
    public BlockTrapdoorBamboo() {
        this(0);
    }

    public BlockTrapdoorBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Bamboo Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BAMBOO_BLOCK_COLOR;
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_BAMBOO_WOOD_TRAPDOOR);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_BAMBOO_WOOD_TRAPDOOR);
    }
}
