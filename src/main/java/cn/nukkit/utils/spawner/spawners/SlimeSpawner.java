package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntitySlime;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;

public class SlimeSpawner extends AbstractEntitySpawner {

    public SlimeSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntitySlime.class, SpawnerType.MOB);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);

        boolean isSwamp = biomeId == 6 || biomeId == 134;
        boolean isNight = !level.isDaytime();

        if (isSwamp && isNight && pos.y < 90) {
            if (level.isMobSpawningAllowedByTime()) {
                this.spawnTask.createEntity("Slime", pos.add(0.5, 1, 0.5));
            }
        }
    }

}
