package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Sound;
import cn.nukkit.block.data.BlockColor;

public class BlockFenceGateWarped extends BlockFenceGate {

    public BlockFenceGateWarped() {
        this(0);
    }

    public BlockFenceGateWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Warped Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public void playOpenSound() {
        level.addSound(this, Sound.OPEN_NETHER_WOOD_FENCE_GATE);
    }

    @Override
    public void playCloseSound() {
        level.addSound(this, Sound.CLOSE_NETHER_WOOD_FENCE_GATE);
    }
}