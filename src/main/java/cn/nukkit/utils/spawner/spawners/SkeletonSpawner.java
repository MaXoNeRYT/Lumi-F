package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;

public class SkeletonSpawner extends AbstractEntitySpawner {

    public SkeletonSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntitySkeleton.class, SpawnerType.MOB);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (level.isMobSpawningAllowedByTime()) {
            this.spawnTask.createEntity("Skeleton", pos.add(0.5, 1, 0.5));
        }
    }
}
