package cn.nukkit.block;

import cn.nukkit.level.Sound;
import cn.nukkit.block.data.BlockColor;

public class BlockTrapdoorCrimson extends BlockTrapdoor {

    public BlockTrapdoorCrimson() {
        this(0);
    }

    public BlockTrapdoorCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Crimson Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
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
