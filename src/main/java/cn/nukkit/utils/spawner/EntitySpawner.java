package cn.nukkit.utils.spawner;

import cn.nukkit.Player;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

/**
 * Interface of a mob spawner
 */
public interface EntitySpawner {

    enum SpawnerType {
        ANIMAL,
        MOB,
        CUSTOM,
        WATER,
        AMBIENT
    }

    /**
     * Find safe coordinates and attempt to spawn a mob
     */
    void spawn();

    /**
     * Run the spawner
     *
     * @param player player
     * @param pos safe position
     * @param level world
     */
    void spawn(Player player, Position pos, Level level);

    /**
     * Get entity class of this mob spawner
     *
     * @return entity class
     */
    Class<? extends BaseEntity> getEntityClass();

    /**
     * Get spawner type
     *
     * @return spawner type
     */
    SpawnerType getSpawnerType();

    default boolean isWaterMob() {
        return false;
    }
}