package cn.nukkit.utils.spawner;

import cn.nukkit.Difficulty;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLava;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityPhantom;
import cn.nukkit.entity.passive.EntityStrider;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class of the default mob spawners
 */
public abstract class AbstractEntitySpawner implements EntitySpawner {

    protected EntitySpawnerTask spawnTask;
    protected Class<? extends BaseEntity> entityClass;
    protected SpawnerType spawnerType;
    protected List<String> allowedWorlds = new ArrayList<>();
    private static final int MAX_MOBS_PER_PLAYER = 23;
    private static final int MOB_TRACKING_RADIUS = 128;
    protected boolean ignoreMaxSpawnRules = false;

    public AbstractEntitySpawner(EntitySpawnerTask spawnTask, Class<? extends BaseEntity> entityClass, SpawnerType spawnerType) {
        this.spawnTask = spawnTask;
        this.entityClass = entityClass;
        this.spawnerType = spawnerType;
    }

    @Override
    public Class<? extends BaseEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public SpawnerType getSpawnerType() {
        return spawnerType;
    }

    @Override
    public void spawn() {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            if (!allowedWorlds.isEmpty()) {
                String worldName = player.getLevel().getName().toLowerCase();
                if (!allowedWorlds.contains(worldName)) {
                    continue;
                }
            }
            if (isSpawningAllowed(player)) {
                spawnTo(player);
            }
        }
    }

    public void setIgnoreMaxSpawnRules(boolean ignore) {
        this.ignoreMaxSpawnRules = ignore;
    }

    private int countMobsNearPlayer(Player player) {
        Level level = player.getLevel();
        int count = 0;

        for (Entity entity : level.getEntities()) {
            if (entity instanceof BaseEntity && entity.isAlive() && !entity.isClosed()) {
                BaseEntity baseEntity = (BaseEntity) entity;

                // Игнорируем мобов с тегом "nodespawn"
                if (baseEntity.namedTag.getBoolean("nodespawn")) {
                    continue;
                }

                if (baseEntity.distanceSquared(player) <= MOB_TRACKING_RADIUS * MOB_TRACKING_RADIUS) {
                    count++;
                    if (count >= MAX_MOBS_PER_PLAYER) {
                        break;
                    }
                }
            }
        }

        return count;
    }


    private boolean canSpawnMoreMobs(Player player) {
        return countMobsNearPlayer(player) < MAX_MOBS_PER_PLAYER;
    }

    public void setAllowedWorlds(String... worlds) {
        allowedWorlds.clear();
        for (String world : worlds) {
            allowedWorlds.add(world.toLowerCase());
        }
    }

    /**
     * Attempt to spawn a mob to a player
     */
    /**
     * Attempt to spawn a mob to a player
     */
    private void spawnTo(Player player) {
        if (!canSpawnMoreMobs(player)) {
            return;
        }

        Level level = player.getLevel();
        Position pos = new Position(player.getFloorX(), player.getFloorY(), player.getFloorZ(), level);

        if (EntitySpawnerTask.entitySpawnAllowed(level, this.getEntityClass(), player, this)) {
            if (entityClass == EntityPhantom.class) {
                if (!level.isInSpawnRadius(pos)) {
                    pos.x = pos.x + Utils.rand(-2, 2);
                    pos.y = pos.y + Utils.rand(20, 34);
                    pos.z = pos.z + Utils.rand(-2, 2);
                    spawn(player, pos, level);
                }
            } else {
                pos.x += EntitySpawnerTask.getRandomSafeXZCoord(Utils.rand(48, 52), Utils.rand(24, 28), Utils.rand(4, 8));
                pos.z += EntitySpawnerTask.getRandomSafeXZCoord(Utils.rand(48, 52), Utils.rand(24, 28), Utils.rand(4, 8));

                FullChunk chunk = level.getChunkIfLoaded((int) pos.x >> 4, (int) pos.z >> 4);
                if (chunk == null || !chunk.isGenerated() || !chunk.isPopulated()) {
                    return;
                }

                if (level.isInSpawnRadius(pos)) {
                    return;
                }

                if (spawnerType == SpawnerType.MOB) {
                    int biome = chunk.getBiomeId(((int) pos.x) & 0x0f, ((int) pos.z) & 0x0f);
                    if (biome == 14 || biome == 15) {
                        return;
                    }
                }

                pos.y = EntitySpawnerTask.getSafeYCoord(level, pos);

                if (this.isWaterMob()) {
                    pos.y--;
                }

                if (pos.y <= -64 || pos.y > level.getMaxBlockY() || level.getDimension() == 1 && pos.y > 125.0) {
                    return;
                }

                if (AbstractEntitySpawner.isTooNearOfPlayer(pos)) {
                    return;
                }

                Block block = level.getBlock(pos, false);
                if (entityClass == EntityStrider.class) {
                    if (!(block instanceof BlockLava)) {
                        return;
                    }
                } else {
                    if (block.getId() == Block.BROWN_MUSHROOM_BLOCK || block.getId() == Block.RED_MUSHROOM_BLOCK) {
                        return;
                    }

                    if (block.isTransparent() && block.getId() != Block.SNOW_LAYER) {
                        if ((block.getId() != Block.WATER && block.getId() != Block.STILL_WATER) || !this.isWaterMob()) {
                            return;
                        }
                    }
                }

                if (spawnerType == SpawnerType.MOB) {
                    if (!isDarkEnoughToSpawn(level, pos)) {
                        return;
                    }
                }

                try {
                    this.spawn(player, pos, level);
                } catch (Exception e) {
                    Server.getInstance().getLogger().error("Error while spawning entity", e);
                }
            }
        }
    }

    /**
     * Check if light level is low enough for hostile mobs to spawn
     * Similar to vanilla Minecraft - mobs spawn at light level 7 or below
     */
    private boolean isDarkEnoughToSpawn(Level level, Position pos) {
        if (this.ignoreMaxSpawnRules) {
            return true;
        }

        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        int blockLight = level.getBlockLightAt(x, y, z);
        int skyLight = level.getBlockSkyLightAt(x, y, z);

        return blockLight <= 7 && skyLight <= 7;
    }

    /**
     * Check if mob spawning is allowed
     */
    private boolean isSpawningAllowed(Player player) {
        if (player.isSpectator()) {
            return false;
        }
        if (!player.getLevel().isMobSpawningAllowed() || Utils.rand(1, 4) == 1) {
            return false;
        }
        if (Server.getInstance().getDifficulty() == Difficulty.PEACEFUL) {
            return spawnerType != SpawnerType.MOB;
        }
        return true;
    }

    private static boolean isTooNearOfPlayer(Position pos) {
        for (Player p : pos.getLevel().getPlayers().values()) {
            if (p.distanceSquared(pos) < 196) {
                return true;
            }
        }
        return false;
    }
}