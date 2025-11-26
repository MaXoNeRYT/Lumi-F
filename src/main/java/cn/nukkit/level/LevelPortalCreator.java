package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import static cn.nukkit.level.Level.DIMENSION_THE_END;

public class LevelPortalCreator {
    final int maxPortalSize = 23;

    Level level;
    LevelPortalCreator(Level level) {
        this.level = level;
    };

    public boolean create(Block target, boolean fireCharge) {
        if (level.getDimension() == DIMENSION_THE_END) return false;
        final int targX = target.getFloorX();
        final int targY = target.getFloorY();
        final int targZ = target.getFloorZ();

        // Check air above target
        for (int i = 1; i < 4; i++) {
            if (level.getBlockIdAt(targX, targY + i, targZ) != BlockID.AIR) {
                return false;
            }
        }

        // Calculate frame dimensions in all directions
        int sizePosX = countObsidianInDirection(targX, targY, targZ, 1, 0);
        int sizeNegX = countObsidianInDirection(targX, targY, targZ, -1, 0);
        int sizePosZ = countObsidianInDirection(targX, targY, targZ, 0, 1);
        int sizeNegZ = countObsidianInDirection(targX, targY, targZ, 0, -1);

        int sizeX = sizePosX + sizeNegX + 1;
        int sizeZ = sizePosZ + sizeNegZ + 1;

        // Try X-axis portal first
        if (sizeX >= 2 && sizeX <= maxPortalSize) {
            PortalScanResult scan = findPortalStartX(targX, targY, targZ, sizePosX, sizeNegX);
            if (scan != null && validateAndCreatePortal(scan, fireCharge, target)) {
                return true;
            }
        }

        // Try Z-axis portal
        if (sizeZ >= 2 && sizeZ <= maxPortalSize) {
            PortalScanResult scan = findPortalStartZ(targX, targY, targZ, sizePosZ, sizeNegZ);
            return scan != null && validateAndCreatePortal(scan, fireCharge, target);
        }

        return false;
    }

    private int countObsidianInDirection(int x, int y, int z, int dirX, int dirZ) {
        int count = 0;
        for (int i = 1; i < maxPortalSize; i++) {
            if (level.getBlockIdAt(x + dirX * i, y, z + dirZ * i) == BlockID.OBSIDIAN) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private PortalScanResult findPortalStartX(int targX, int targY, int targZ, int sizePosX, int sizeNegX) {
        int scanY = targY + 1;

        // Try positive X direction first (original logic)
        for (int i = 0; i <= sizePosX; i++) {
            int checkX = targX + i;
            if (level.getBlockIdAt(checkX, scanY, targZ) != BlockID.AIR) {
                break;
            }
            if (level.getBlockIdAt(checkX + 1, scanY, targZ) == BlockID.OBSIDIAN) {
                return new PortalScanResult(checkX, scanY, targZ, true);
            }
        }

        // Try negative X direction (fix for left-side ignition)
        for (int i = 0; i <= sizeNegX; i++) {
            int checkX = targX - i;
            if (level.getBlockIdAt(checkX, scanY, targZ) != BlockID.AIR) {
                break;
            }
            if (level.getBlockIdAt(checkX - 1, scanY, targZ) == BlockID.OBSIDIAN) {
                return new PortalScanResult(checkX, scanY, targZ, true);
            }
        }

        return null;
    }

    private PortalScanResult findPortalStartZ(int targX, int targY, int targZ, int sizePosZ, int sizeNegZ) {
        int scanY = targY + 1;

        // Try positive Z direction first (original logic)
        for (int i = 0; i <= sizePosZ; i++) {
            int checkZ = targZ + i;
            if (level.getBlockIdAt(targX, scanY, checkZ) != BlockID.AIR) {
                break;
            }
            if (level.getBlockIdAt(targX, scanY, checkZ + 1) == BlockID.OBSIDIAN) {
                return new PortalScanResult(targX, scanY, checkZ, false);
            }
        }

        // Try negative Z direction
        for (int i = 0; i <= sizeNegZ; i++) {
            int checkZ = targZ - i;
            if (level.getBlockIdAt(targX, scanY, checkZ) != BlockID.AIR) {
                break;
            }
            if (level.getBlockIdAt(targX, scanY, checkZ - 1) == BlockID.OBSIDIAN) {
                return new PortalScanResult(targX, scanY, checkZ, false);
            }
        }

        return null;
    }

    private boolean validateAndCreatePortal(PortalScanResult scan, boolean fireCharge, Block target) {
        int innerWidth = findInnerWidth(scan);
        int innerHeight = findInnerHeight(scan);

        if (innerWidth < 2 || innerWidth > 21 || innerHeight < 3 || innerHeight > 21) {
            return false;
        }

        if (!validatePortalStructure(scan, innerWidth, innerHeight)) {
            return false;
        }

        createPortalBlocks(scan, innerWidth, innerHeight);
        playPortalEffects(target, fireCharge);
        return true;
    }

    private int findInnerWidth(PortalScanResult scan) {
        int width = 0;
        int dirX = scan.isXAxis ? -1 : 0;
        int dirZ = scan.isXAxis ? 0 : -1;

        for (int i = 0; i < 21; i++) {
            int checkX = scan.startX + dirX * i;
            int checkZ = scan.startZ + dirZ * i;
            int id = level.getBlockIdAt(checkX, scan.startY, checkZ);

            if (id == BlockID.AIR) {
                width++;
            } else if (id == BlockID.OBSIDIAN) {
                break;
            } else {
                return -1; // Invalid block
            }
        }
        return width;
    }

    private int findInnerHeight(PortalScanResult scan) {
        int height = 0;
        for (int i = 0; i < 21; i++) {
            int id = level.getBlockIdAt(scan.startX, scan.startY + i, scan.startZ);
            if (id == BlockID.AIR) {
                height++;
            } else if (id == BlockID.OBSIDIAN) {
                break;
            } else {
                return -1; // Invalid block
            }
        }
        return height;
    }

    private boolean validatePortalStructure(PortalScanResult scan, int innerWidth, int innerHeight) {
        // Validate top row
        if (!validateTopRow(scan, innerWidth, innerHeight)) {
            return false;
        }

        // Validate sides and interior for each level
        for (int height = 0; height < innerHeight; height++) {
            if (!validatePortalLevel(scan, innerWidth, height)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateTopRow(PortalScanResult scan, int innerWidth, int innerHeight) {
        int dirX = scan.isXAxis ? -1 : 0;
        int dirZ = scan.isXAxis ? 0 : -1;

        for (int width = 0; width < innerWidth; width++) {
            int checkX = scan.startX + dirX * width;
            int checkZ = scan.startZ + dirZ * width;
            if (level.getBlockIdAt(checkX, scan.startY + innerHeight, checkZ) != BlockID.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }

    private boolean validatePortalLevel(PortalScanResult scan, int innerWidth, int height) {
        int dirX = scan.isXAxis ? -1 : 0;
        int dirZ = scan.isXAxis ? 0 : -1;

        // Check right/front pillar
        int pillar1X = scan.startX + (scan.isXAxis ? 1 : 0);
        int pillar1Z = scan.startZ + (scan.isXAxis ? 0 : 1);
        if (level.getBlockIdAt(pillar1X, scan.startY + height, pillar1Z) != BlockID.OBSIDIAN) {
            return false;
        }

        // Check left/back pillar
        int pillar2X = scan.startX + dirX * innerWidth;
        int pillar2Z = scan.startZ + dirZ * innerWidth;
        if (level.getBlockIdAt(pillar2X, scan.startY + height, pillar2Z) != BlockID.OBSIDIAN) {
            return false;
        }

        // Check interior is air
        for (int width = 0; width < innerWidth; width++) {
            int checkX = scan.startX + dirX * width;
            int checkZ = scan.startZ + dirZ * width;
            if (level.getBlockIdAt(checkX, scan.startY + height, checkZ) != BlockID.AIR) {
                return false;
            }
        }

        return true;
    }

    private void createPortalBlocks(PortalScanResult scan, int innerWidth, int innerHeight) {
        int dirX = scan.isXAxis ? -1 : 0;
        int dirZ = scan.isXAxis ? 0 : -1;

        for (int height = 0; height < innerHeight; height++) {
            for (int width = 0; width < innerWidth; width++) {
                int portalX = scan.startX + dirX * width;
                int portalZ = scan.startZ + dirZ * width;
                level.setBlock(new Vector3(portalX, scan.startY + height, portalZ),
                        Block.get(BlockID.NETHER_PORTAL));
            }
        }
    }

    private void playPortalEffects(Block target, boolean fireCharge) {
        if (fireCharge) {
            level.addSoundToViewers(target, cn.nukkit.level.Sound.MOB_GHAST_FIREBALL);
        } else {
            level.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_IGNITE);
        }
    }

    // Simple data class to hold portal scan results
    private record PortalScanResult(int startX, int startY, int startZ, boolean isXAxis) {
    }
}
