package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

import java.util.Random;

public abstract class TreeGenerator extends cn.nukkit.level.generator.object.BasicGenerator {

    /**
     * returns whether or not a tree can grow into a block
     * For example, a tree will not grow into stone
     */
    protected boolean canGrowInto(int id) {
        return switch (id) {
            case Item.AIR, Item.LEAVES, Item.GRASS, Item.DIRT, BlockID.OAK_LOG, BlockID.SPRUCE_LOG, BlockID.BIRCH_LOG, BlockID.JUNGLE_LOG, BlockID.ACACIA_LOG, BlockID.DARK_OAK_LOG, Item.SAPLING, Item.VINE,
                    Item.DIRT_WITH_ROOTS, Item.AZALEA_LEAVES, Item.AZALEA_LEAVES_FLOWERED,
                 Item.CHERRY_LEAVES, Item.CHERRY_LOG, Item.CHERRY_SAPLING, BlockID.PALE_OAK_SAPLING, BlockID.PALE_OAK_LEAVES -> true;
            default -> false;
        };
    }

    public void generateSaplings(Level level, Random random, Vector3 pos) {
    }

    protected void setDirtAt(ChunkManager level, BlockVector3 pos) {
        setDirtAt(level, new Vector3(pos.x, pos.y, pos.z));
    }

    /**
     * sets dirt at a specific location if it isn't already dirt
     */
    protected void setDirtAt(ChunkManager level, Vector3 pos) {
        if (level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != Item.DIRT) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(BlockID.DIRT));
        }
    }
}
