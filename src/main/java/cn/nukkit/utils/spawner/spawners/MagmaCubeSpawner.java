package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityMagmaCube;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class MagmaCubeSpawner extends AbstractEntitySpawner {

    public MagmaCubeSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityMagmaCube.class, SpawnerType.MOB);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            this.spawnTask.createEntity("MagmaCube", pos.add(0.5, 1, 0.5));
        }
    }
}
