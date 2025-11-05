package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityEnderman;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class EndermanSpawner extends AbstractEntitySpawner {

    public EndermanSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityEnderman.class, SpawnerType.MOB);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        boolean nether = level.getDimension() == Level.DIMENSION_NETHER;
        boolean end = level.getDimension() == Level.DIMENSION_THE_END;

        if (!nether && !end && !level.isMobSpawningAllowedByTime()) {
            return;
        }

        if (!end && Utils.rand(1, nether ? 10 : 7) != 1) {
            return;
        }

        if (end) {
            for (int i = 0; i < Utils.rand(1, 4); i++) {
                this.spawnTask.createEntity("Enderman", pos.add(0.5, 1, 0.5));
            }
        } else {
            this.spawnTask.createEntity("Enderman", pos.add(0.5, 1, 0.5));
        }
    }
}
