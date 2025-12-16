package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

/**
 * @author MaXoNeRYT
 * @since 2025/07/19
 */
public class ObjectAzaleaTree extends TreeGenerator {
    private static final Block LOG = Block.get(Block.OAK_LOG);
    private static final Block AZALEA_LEAVES = Block.get(Block.AZALEA_LEAVES);
    private static final Block FLOWERING_AZALEA_LEAVES = Block.get(Block.AZALEA_LEAVES_FLOWERED);
    private static final Block ROOTED_DIRT = Block.get(Block.ROOTED_DIRT);
    private boolean flowering;

    public boolean grow(ChunkManager level, NukkitRandom rand, Vector3 position, boolean flowering) {
        this.flowering = flowering;
        return generate(level, rand, position);
    }

    @Override
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        if (y < -64 || y >= 320) return false;

        if(flowering) this.setBlockAndNotifyAdequately(level, new Vector3(x, y - 1, z), ROOTED_DIRT);

        int height = 4 + rand.nextBoundedInt(3);
        int bendHeight = 2 + rand.nextBoundedInt(2);

        int bendX = 0, bendZ = 0;
        switch(rand.nextBoundedInt(4)) {
            case 0: bendX = 1; break;
            case 1: bendX = -1; break;
            case 2: bendZ = 1; break;
            case 3: bendZ = -1; break;
        }

        for (int i = 0; i < height; i++) {
            Vector3 logPos = new Vector3(x, y + i, z);

            if (i >= bendHeight) {
                logPos = logPos.add(
                        bendX * Math.min(1, i - bendHeight + 1),
                        0,
                        bendZ * Math.min(1, i - bendHeight + 1)
                );
            }

            placeLogAt(level, logPos.getFloorX(), logPos.getFloorY(), logPos.getFloorZ());
        }

        int topY = y + height - 1;

        int leafPlacementAttempts = 50;

        for (int i = 0; i < leafPlacementAttempts; i++) {
            int offsetX = rand.nextRange(-2, 2);
            int offsetY = rand.nextRange(-1, 1);
            int offsetZ = rand.nextRange(-2, 2);

            Block leaves = rand.nextBoundedInt(4) == 0 ?
                    FLOWERING_AZALEA_LEAVES : AZALEA_LEAVES;

            placeLeafAt(level, x + offsetX, topY + offsetY, z + offsetZ, leaves);
        }

        for (int dy = 0; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0 && dy == 1) continue;

                    Block leaves = rand.nextBoundedInt(4) == 0 ?
                            FLOWERING_AZALEA_LEAVES : AZALEA_LEAVES;

                    placeLeafAt(level, x + dx, topY + dy, z + dz, leaves);
                }
            }
        }

        return true;
    }

    private void placeLogAt(ChunkManager level, int x, int y, int z) {
        Vector3 pos = new Vector3(x, y, z);
        if (canGrowInto(level, pos)) {
            this.setBlockAndNotifyAdequately(level, pos, LOG);
        }
    }

    private void placeLeafAt(ChunkManager level, int x, int y, int z, Block leaves) {
        Vector3 pos = new Vector3(x, y, z);
        if (canGrowInto(level, pos)) {
            this.setBlockAndNotifyAdequately(level, pos, leaves);
        }
    }

    private boolean canGrowInto(ChunkManager level, Vector3 pos) {
        int id = level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
        return id == Block.AIR || id == Block.AZALEA_LEAVES || id == Block.AZALEA_LEAVES_FLOWERED;
    }
}