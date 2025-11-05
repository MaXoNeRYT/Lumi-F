package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityBat;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;

public class BatSpawner extends AbstractEntitySpawner {

    public BatSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityBat.class, SpawnerType.AMBIENT);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (!level.canBlockSeeSky(pos)) {
            if (level.isAnimalSpawningAllowedByTime()) {
                this.spawnTask.createEntity("Bat", pos.add(0.5, 1, 0.5));
            }
        }
    }
}
