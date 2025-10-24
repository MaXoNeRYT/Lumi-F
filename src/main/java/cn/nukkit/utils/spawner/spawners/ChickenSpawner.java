package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class ChickenSpawner extends AbstractEntitySpawner {

    public ChickenSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityChicken.class, SpawnerType.ANIMAL);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        if (level.isAnimalSpawningAllowedByTime()) {
            int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
            if (blockId == Block.GRASS || blockId == Block.SNOW_LAYER) {
                for (int i = 0; i < Utils.rand(2, 4); i++) {
                    BaseEntity entity = this.spawnTask.createEntity("Chicken", pos.add(0.5, 1, 0.5));
                    if (entity == null) return;
                    if (Utils.rand(1, 20) == 1) {
                        entity.setBaby(true);
                    }
                }
            }
        }
    }
}
