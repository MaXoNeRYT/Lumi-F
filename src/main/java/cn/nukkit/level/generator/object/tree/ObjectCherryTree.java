package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLog;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

/**
 * @author MaXoNeRYT
 * @since 2025/07/19
 */
public class ObjectCherryTree extends TreeGenerator {
    private static final Block CHERRY_LOG = Block.get(BlockID.CHERRY_LOG, BlockLog.faces[BlockFace.DOWN.getIndex()]);
    private static final Block CHERRY_LEAVES = Block.get(BlockID.CHERRY_LEAVES);
    private static final Block DIRT = Block.get(BlockID.DIRT);

    @Override
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        if (y < -64 || y >= 256) return false;

        this.setDirtAt(level, new Vector3(x, y - 1, z));

        int trunkHeight = 7 + rand.nextBoundedInt(2);
        int branchCount = rand.nextBoundedInt(3) + 1;
        boolean hasDoubleBranch = branchCount >= 2;

        for (int dy = 0; dy < trunkHeight; dy++) {
            setBlockIfAir(level, x, y + dy, z, CHERRY_LOG);
        }

        int branchStartOffset = -4 + rand.nextBoundedInt(3);
        int branchEndOffset = -1 + rand.nextBoundedInt(2);

        generateBranch(level, rand, x, y, z, trunkHeight, branchStartOffset, branchEndOffset);

        if (hasDoubleBranch) {
            int secondBranchOffset = branchStartOffset - 1;
            generateBranch(level, rand, x, y, z, trunkHeight, secondBranchOffset, branchEndOffset);
        }


        if (branchCount == 3) {
            generateTopBranch(level, rand, x, y, z, trunkHeight);
        }

        generateLeaves(level, rand, x, y + trunkHeight, z);

        return true;
    }

    private void generateBranch(ChunkManager level, NukkitRandom rand, int baseX, int baseY, int baseZ,
                                int trunkHeight, int branchStartOffset, int branchEndOffset) {
        int branchStartY = baseY + trunkHeight + branchStartOffset;
        if (branchStartY < baseY) branchStartY = baseY;

        int branchEndY = baseY + trunkHeight + branchEndOffset;

        BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        BlockFace direction = faces[rand.nextBoundedInt(4)];

        int length = 2 + rand.nextBoundedInt(3);

        int dx = 0, dz = 0;
        for (int i = 1; i <= length; i++) {
            dx = direction.getXOffset() * i;
            dz = direction.getZOffset() * i;

            setBlockIfAir(level, baseX + dx, branchStartY, baseZ + dz,
                    getLogWithAxis(direction.getAxis() == BlockFace.Axis.X ? BlockFace.Axis.X : BlockFace.Axis.Z));
        }

        int yDir = branchEndY > branchStartY ? 1 : -1;
        int currentY = branchStartY;
        int targetY = branchEndY;

        while (currentY != targetY) {
            currentY += yDir;
            setBlockIfAir(level, baseX + dx, currentY, baseZ + dz, CHERRY_LOG);
        }

        generateLeaves(level, rand, baseX + dx, currentY, baseZ + dz);
    }

    private void generateTopBranch(ChunkManager level, NukkitRandom rand, int baseX, int baseY, int baseZ, int trunkHeight) {
        int topY = baseY + trunkHeight;
        setBlockIfAir(level, baseX, topY, baseZ, CHERRY_LOG);
        generateLeaves(level, rand, baseX, topY, baseZ);
    }

    private void generateLeaves(ChunkManager level, NukkitRandom rand, int centerX, int centerY, int centerZ) {
        for (int dy = -2; dy <= 2; dy++) {
            int radius = 4 - Math.max(1, Math.abs(dy));

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx*dx + dz*dz > radius*radius) continue;

                    if (dy == -2 && rand.nextBoundedInt(4) == 0) continue;

                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && rand.nextBoundedInt(6) == 0) continue;

                    setLeafIfAir(level, centerX + dx, centerY + dy, centerZ + dz);

                    if (dy == -2 && rand.nextBoundedInt(6) == 0) {
                        setLeafIfAir(level, centerX + dx, centerY + dy - 1, centerZ + dz);

                        if (rand.nextBoundedInt(3) == 0) {
                            setLeafIfAir(level, centerX + dx, centerY + dy - 2, centerZ + dz);
                        }
                    }
                }
            }
        }
    }

    private Block getLogWithAxis(BlockFace.Axis axis) {
        if (axis == BlockFace.Axis.X) {
            return Block.get(BlockID.CHERRY_LOG, BlockLog.faces[BlockFace.WEST.getIndex()]);
        } else if (axis == BlockFace.Axis.Z) {
            return Block.get(BlockID.CHERRY_LOG, BlockLog.faces[BlockFace.NORTH.getIndex()]);
        }
        return CHERRY_LOG;
    }

    private void setBlockIfAir(ChunkManager level, int x, int y, int z, Block block) {
        if (canGrowInto(level.getBlockIdAt(x, y, z))) {
            this.setBlockAndNotifyAdequately(level, new Vector3(x, y, z), block);
        }
    }

    private void setLeafIfAir(ChunkManager level, int x, int y, int z) {
        int id = level.getBlockIdAt(x, y, z);
        if (id == BlockID.AIR || id == BlockID.CHERRY_LEAVES) {
            this.setBlockAndNotifyAdequately(level, new Vector3(x, y, z), CHERRY_LEAVES);
        }
    }

    public boolean canGrowInto(int blockId) {
        return blockId == BlockID.AIR || blockId == BlockID.CHERRY_LEAVES ||
                blockId == BlockID.LEAVES || blockId == BlockID.LEAVES2 ||
                blockId == BlockID.CHERRY_SAPLING ||
                blockId == BlockID.AZALEA_LEAVES || blockId == BlockID.AZALEA_LEAVES_FLOWERED;
    }
}