package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;

/**
 * Universal helper for obtaining the correct AxisAlignedBB of any block,
 * taking its current world state (meta/damage) into account.
 *
 * The problem this solves:
 *   Blocks like BlockDoor compute their bounding box dynamically inside
 *   recalculateBoundingBox() by calling this.up() / this.down().
 *   Those calls only work when the block object carries a valid position
 *   (x, y, z, level).  A block fetched via level.getBlock(x, y, z) already
 *   has all of that, so we just need to make sure we never call getBoundingBox()
 *   on a bare, unpositioned clone.
 */
public final class BlockBoundingBoxHelper {

    private BlockBoundingBoxHelper() {}

    /**
     * Returns the bounding box for the block at (x, y, z) in the given level,
     * fully reflecting the block's current meta/state.
     *
     * Always use this method instead of block.getBoundingBox() when the block
     * object may not have its position set (e.g. blocks obtained from a registry
     * clone, passed as a parameter, etc.).
     *
     * @param level the world
     * @param x     block X
     * @param y     block Y
     * @param z     block Z
     * @return the bounding box, or {@code null} if the block has none
     */
    public static AxisAlignedBB getBoundingBox(Level level, int x, int y, int z) {
        // level.getBlock() returns a positioned clone with the correct damage value
        // already applied, so recalculateBoundingBox() inside any block subclass
        // (including BlockDoor's up()/down() calls) will work correctly.
        Block block = level.getBlock(x, y, z);
        return block.getBoundingBox();
    }

    /**
     * Convenience overload: if you already hold a block object but aren't sure
     * whether it has a valid position, this will re-fetch it from the level to
     * guarantee correctness.
     *
     * @param level  the world
     * @param block  any block (used only for its coordinates)
     * @return the bounding box, or {@code null} if the block has none
     */
    public static AxisAlignedBB getBoundingBox(Level level, Block block) {
        return getBoundingBox(level, block.getFloorX(), block.getFloorY(), block.getFloorZ());
    }
}