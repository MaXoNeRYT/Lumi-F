package cn.nukkit.block;

import cn.nukkit.block.data.BlockColor;
import cn.nukkit.level.Sound;

public class BlockTrapdoorPaleOak extends BlockTrapdoor {
    public BlockTrapdoorPaleOak() {
        this(0);
    }

    public BlockTrapdoorPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Pale Oak Trapdoor";
    }
}
