package cn.nukkit.utils.spawner;

import cn.nukkit.Difficulty;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.EntityCod;
import cn.nukkit.entity.passive.EntitySalmon;
import cn.nukkit.entity.passive.EntityStrider;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.spawner.spawners.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the automatic spawning of mobs.
 */
public class EntitySpawnerTask implements Runnable {

    public final Map<Class<?>, EntitySpawner> animalSpawners = new HashMap<>();
    public final Map<Class<?>, EntitySpawner> mobSpawners = new HashMap<>();
    public final List<EntitySpawner> customSpawners = new ArrayList<>();
    private static final List<EntitySpawnerRegistration> registrationHooks = new ArrayList<>();
    public final Map<String, EntitySpawner> pluginAnimalSpawners = new HashMap<>();
    public final Map<String, EntitySpawner> pluginMobSpawners = new HashMap<>();


    private boolean mobsNext;

    public EntitySpawnerTask() {
        this.registerDefaultSpawners();
    }

    private void registerDefaultSpawners() {
        this.registerAnimalSpawner(BatSpawner.class);
        this.registerAnimalSpawner(ChickenSpawner.class);
        this.registerAnimalSpawner(CowSpawner.class);
        this.registerAnimalSpawner(DolphinSpawner.class);
        this.registerAnimalSpawner(DonkeySpawner.class);
        this.registerAnimalSpawner(HorseSpawner.class);
        this.registerAnimalSpawner(LlamaSpawner.class);
        this.registerAnimalSpawner(MooshroomSpawner.class);
        this.registerAnimalSpawner(OcelotSpawner.class);
        this.registerAnimalSpawner(ParrotSpawner.class);
        this.registerAnimalSpawner(PigSpawner.class);
        this.registerAnimalSpawner(PolarBearSpawner.class);
        this.registerAnimalSpawner(PufferfishSpawner.class);
        this.registerAnimalSpawner(RabbitSpawner.class);
        this.registerAnimalSpawner(SalmonSpawner.class);
        this.registerAnimalSpawner(SheepSpawner.class);
        this.registerAnimalSpawner(SquidSpawner.class);
        this.registerAnimalSpawner(StriderSpawner.class);
        this.registerAnimalSpawner(TropicalFishSpawner.class);
        this.registerAnimalSpawner(TurtleSpawner.class);
        this.registerAnimalSpawner(WolfSpawner.class);
        this.registerAnimalSpawner(PandaSpawner.class);
        this.registerAnimalSpawner(FoxSpawner.class);

        this.registerMobSpawner(BlazeSpawner.class);
        this.registerMobSpawner(CreeperSpawner.class);
        this.registerMobSpawner(EndermanSpawner.class);
        this.registerMobSpawner(GhastSpawner.class);
        this.registerMobSpawner(HuskSpawner.class);
        this.registerMobSpawner(MagmaCubeSpawner.class);
        this.registerMobSpawner(SkeletonSpawner.class);
        this.registerMobSpawner(SlimeSpawner.class);
        this.registerMobSpawner(SpiderSpawner.class);
        this.registerMobSpawner(StraySpawner.class);
        this.registerMobSpawner(ZombieSpawner.class);
        this.registerMobSpawner(ZombiePigmanSpawner.class);
        this.registerMobSpawner(WitchSpawner.class);
        this.registerMobSpawner(WitherSkeletonSpawner.class);
        this.registerMobSpawner(DrownedSpawner.class);
        this.registerMobSpawner(PhantomSpawner.class);
        this.registerMobSpawner(PiglinSpawner.class);
        this.registerMobSpawner(HoglinSpawner.class);
        for (EntitySpawnerRegistration hook : registrationHooks) {
            try {
                hook.register(this);
            } catch (Exception e) {
                Server.getInstance().getLogger().error("Failed to register plugin spawners", e);
            }
        }
    }


    public static void addRegistrationHook(EntitySpawnerRegistration hook) {
        registrationHooks.add(hook);
    }

    public interface EntitySpawnerRegistration {
        void register(EntitySpawnerTask task);
    }

    @Override
    public void run() {
        if (Server.getInstance().getOnlinePlayersCount() != 0) {
            if (mobsNext) {
                mobsNext = false;
                if (Server.getInstance().getSettings().world().entity().spawnMobs()) {
                    for (EntitySpawner spawner : mobSpawners.values()) {
                        spawner.spawn();
                    }
                    for (EntitySpawner spawner : pluginMobSpawners.values()) {
                        spawner.spawn();
                    }
                }
            } else {
                mobsNext = true;
                if (Server.getInstance().getSettings().world().entity().spawnAnimals()) {
                    for (EntitySpawner spawner : animalSpawners.values()) {
                        spawner.spawn();
                    }
                    for (EntitySpawner spawner : pluginAnimalSpawners.values()) {
                        spawner.spawn();
                    }
                }
            }

            for (EntitySpawner spawner : customSpawners) {
                spawner.spawn();
            }
        }
    }

    /**
     * Register animal spawner from plugin by class
     */
    public boolean registerAnimalSpawner(Class<? extends EntitySpawner> clazz) {
        return registerAnimalSpawner(clazz, null);
    }

    /**
     * Register animal spawner from plugin by class with plugin name
     */
    public boolean registerAnimalSpawner(Class<? extends EntitySpawner> clazz, String pluginName) {
        if (this.animalSpawners.containsKey(clazz) ||
                (pluginName != null && this.pluginAnimalSpawners.containsKey(pluginName + ":" + clazz.getSimpleName()))) {
            return false;
        }

        try {
            EntitySpawner spawner = clazz.getConstructor(EntitySpawnerTask.class).newInstance(this);
            if (pluginName != null) {
                this.pluginAnimalSpawners.put(pluginName + ":" + clazz.getSimpleName(), spawner);
            } else {
                this.animalSpawners.put(clazz, spawner);
            }
            return true;
        } catch (Exception e) {
            Server.getInstance().getLogger().error("Failed to register animal spawner: " + clazz.getName(), e);
            return false;
        }
    }

    /**
     * Register mob spawner from plugin by class
     */
    public boolean registerMobSpawner(Class<? extends EntitySpawner> clazz) {
        return registerMobSpawner(clazz, null);
    }

    /**
     * Register mob spawner from plugin by class with plugin name
     */
    public boolean registerMobSpawner(Class<? extends EntitySpawner> clazz, String pluginName) {
        if (this.mobSpawners.containsKey(clazz) ||
                (pluginName != null && this.pluginMobSpawners.containsKey(pluginName + ":" + clazz.getSimpleName()))) {
            return false;
        }

        try {
            EntitySpawner spawner = clazz.getConstructor(EntitySpawnerTask.class).newInstance(this);
            if (pluginName != null) {
                this.pluginMobSpawners.put(pluginName + ":" + clazz.getSimpleName(), spawner);
            } else {
                this.mobSpawners.put(clazz, spawner);
            }
            return true;
        } catch (Exception e) {
            Server.getInstance().getLogger().error("Failed to register mob spawner: " + clazz.getName(), e);
            return false;
        }
    }

    /**
     * Register custom spawner from plugin
     */
    public boolean registerCustomSpawner(EntitySpawner spawner) {
        return registerCustomSpawner(spawner, null);
    }

    /**
     * Register custom spawner from plugin with plugin name
     */
    public boolean registerCustomSpawner(EntitySpawner spawner, String pluginName) {
        if (!customSpawners.contains(spawner)) {
            customSpawners.add(spawner);
            return true;
        }
        return false;
    }

    /**
     * Unregister all spawners from plugin
     */
    public boolean unregisterPluginSpawners(String pluginName) {
        boolean removed = false;

        // Удаляем animal spawners
        removed |= pluginAnimalSpawners.keySet().removeIf(key -> key.startsWith(pluginName + ":"));

        // Удаляем mob spawners
        removed |= pluginMobSpawners.keySet().removeIf(key -> key.startsWith(pluginName + ":"));

        return removed;
    }
    /**
     * Unregister specific spawner from plugin
     */
    public boolean unregisterPluginSpawner(String pluginName, Class<? extends EntitySpawner> clazz) {
        String key = pluginName + ":" + clazz.getSimpleName();
        boolean removed = false;

        removed |= pluginAnimalSpawners.remove(key) != null;
        removed |= pluginMobSpawners.remove(key) != null;

        return removed;
    }

    static boolean entitySpawnAllowed(Level level, Class<? extends BaseEntity> entityClass, Player player, AbstractEntitySpawner spawner) {
        if (spawner.ignoreMaxSpawnRules) {
            return true;
        }

        if (entityClass == EntityPhantom.class &&
                (player.getTimeSinceRest() < 72000 || player.isSleeping() ||
                        player.isSpectator() || !level.getGameRules().getBoolean(GameRule.DO_INSOMNIA))) {
            return false;
        }

        int max = getMaxSpawns(entityClass, level.getDimension() == Level.DIMENSION_NETHER,
                level.getDimension() == Level.DIMENSION_THE_END);
        if (max == 0) return false;

        int count = 0;
        for (Entity entity : level.getEntities()) {
            if (entity.isAlive() && entity.getClass() == entityClass &&
                    new Vector3(player.x, entity.y, player.z).distanceSquared(entity) < 16384) {
                count++;
                if (count > max) {
                    return false;
                }
            }
        }
        return count < max;
    }


    /**
     * Get maximum amount of mobs in distance
     */
    private static int getMaxSpawns(Class<? extends BaseEntity> entityClass, boolean nether, boolean end) {
        if (entityClass == EntityZombiePigman.class || entityClass == EntityPiglin.class || entityClass == EntityHoglin.class) {
            return nether ? 4 : 0;
        } else if (entityClass == EntityGhast.class || entityClass == EntityMagmaCube.class ||
                entityClass == EntityBlaze.class || entityClass == EntityWitherSkeleton.class ||
                entityClass == EntityStrider.class) {
            return nether ? 2 : 0;
        } else if (entityClass == EntityEnderman.class) {
            return end ? 10 : 2;
        } else if (entityClass == EntitySalmon.class || entityClass == EntityCod.class) {
            return end || nether ? 0 : 4;
        } else if (entityClass == EntityWitch.class) {
            return end || nether ? 0 : 1;
        } else if (entityClass == EntityPhantom.class) {
            Difficulty difficulty = Server.getInstance().getDifficulty();
            return end || nether ? 0 : difficulty == Difficulty.EASY ? 2 :
                    difficulty == Difficulty.NORMAL ? 3 : 4;
        } else {
            return end || nether ? 0 : 2;
        }
    }

    /**
     * Attempt to spawn a mob
     *
     * @param type mob id
     * @param pos position
     * @return spawned entity or null
     */
    public BaseEntity createEntity(Object type, Position pos) {
        BaseEntity entity = (BaseEntity) Entity.createEntity((String) type, pos);
        if (entity != null) {
            if (!entity.isInsideOfSolid()) {
                CreatureSpawnEvent ev = new CreatureSpawnEvent(entity.getNetworkId(), pos, entity.namedTag, CreatureSpawnEvent.SpawnReason.NATURAL, null);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    entity.spawnToAll();
                } else {
                    entity.close();
                    entity = null;
                }
            } else {
                entity.close();
                entity = null;
            }
        }
        return entity;
    }

    /**
     * Get safe x / z coordinate for mob spawning
     *
     * @param degree
     * @param safeDegree
     * @param correctionDegree
     * @return safe spawn x / z coordinate
     */
    static int getRandomSafeXZCoord(int degree, int safeDegree, int correctionDegree) {
        int addX = Utils.rand((degree >> 1) * -1, degree >> 1);
        if (addX >= 0) {
            if (degree < safeDegree) {
                addX = safeDegree;
                addX += Utils.rand((correctionDegree >> 1) * -1, correctionDegree >> 1);
            }
        } else {
            if (degree > safeDegree) {
                addX = -safeDegree;
                addX += Utils.rand((correctionDegree >> 1) * -1, correctionDegree >> 1);
            }
        }
        return addX;
    }
    public <T extends BaseEntity> T createEntity(Class<T> clazz, Position pos) {
        try {
            // Получаем чанк
            FullChunk chunk = pos.getLevel().getChunk(pos.getChunkX(), pos.getChunkZ());

            // Создаем NBT
            CompoundTag nbt = Entity.getDefaultNBT(pos.add(0.5, 0, 0.5));

            // Создаем сущность через конструктор FullChunk + NBT
            T entity = clazz.getConstructor(FullChunk.class, CompoundTag.class)
                    .newInstance(chunk, nbt);

            // Проверка на блоки
            if (!entity.isInsideOfSolid()) {
                entity.spawnToAll(); // прямой спавн
                return entity;
            } else {
                entity.close();
            }
        } catch (Exception e) {
            Server.getInstance().getLogger().error("Failed to spawn entity: " + clazz.getSimpleName(), e);
        }
        return null;
    }


    /**
     * Get safe y coordinate for mob spawning
     *
     * @param level world
     * @param pos initial position
     * @return safe spawn y coordinate
     */
    static int getSafeYCoord(Level level, Position pos) {
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        BaseFullChunk chunk = level.getChunk(x >> 4, z >> 4, true);
        if (level.getBlockIdAt(chunk, x, y, z) == Block.AIR) {
            while (true) {
                y--;
                if (y > level.getMaxBlockY()) {
                    y = level.getMaxBlockY() + 1;
                    break;
                }
                if (y < 1) {
                    y = 0;
                    break;
                }
                if (level.getBlockIdAt(chunk, x, y, z) != Block.AIR) {
                    int checkNeedDegree = 3;
                    int checkY = y;
                    while (true) {
                        checkY++;
                        checkNeedDegree--;
                        if (checkY > level.getMaxBlockY() || level.getBlockIdAt(chunk, x, checkY, z) != Block.AIR) {
                            break;
                        }
                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        } else {
            while (true) {
                y++;
                if (y > level.getMaxBlockY()) {
                    y = level.getMaxBlockY() + 1;
                    break;
                }
                if (y < 1) {
                    y = 0;
                    break;
                }
                if (level.getBlockIdAt(chunk, x, y, z) != Block.AIR) {
                    int checkNeedDegree = 3;
                    int checkY = y;
                    while (true) {
                        checkY--;
                        checkNeedDegree--;
                        if (checkY < 1 || level.getBlockIdAt(chunk, x, checkY, z) != Block.AIR) {
                            break;
                        }
                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        }
        return y;
    }

}