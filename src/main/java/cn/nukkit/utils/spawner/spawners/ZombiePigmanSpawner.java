package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.mob.EntityZombiePigman;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class ZombiePigmanSpawner extends AbstractEntitySpawner {

    public ZombiePigmanSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityZombiePigman.class, EntitySpawner.SpawnerType.MOB);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        for (int i = 0; i < Utils.rand(2, 4); i++) {
            BaseEntity entity = this.spawnTask.createEntity("ZombiePigman", pos.add(0.5, 1, 0.5));
            if (entity == null) return;
            if (Utils.rand(1, 20) == 1) {
                entity.setBaby(true);
            }
        }
    }
}
