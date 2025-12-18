package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLog;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ObjectBirchTree extends ObjectTree {

    protected int treeHeight = 7;

    @Override
    public int getType() {
        return 2;
    }

    @Override
    protected int getLeafType() {
        return 2;
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.BIRCH_LOG;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(2) + 5;
        super.placeObject(level, x, y, z, random);
    }
}
