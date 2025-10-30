package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLog;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ObjectJungleTree extends ObjectTree {
    private int treeHeight = 8;

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.JUNGLE_LOG;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(6) + 4;
        super.placeObject(level, x, y, z, random);
    }
}
