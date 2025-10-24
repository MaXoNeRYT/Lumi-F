package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class RabbitSpawner extends AbstractEntitySpawner {

    public RabbitSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityRabbit.class, SpawnerType.ANIMAL);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if (blockId == Block.GRASS || blockId == Block.SNOW_LAYER || blockId == Block.SAND) {
            final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
            if (biomeId == 2 || biomeId == 130 || biomeId == 30 || biomeId == 5 || biomeId == 12 || biomeId == 26 || biomeId == 11) {
                if (level.isAnimalSpawningAllowedByTime()) {
                    for (int i = 0; i < Utils.rand(1, 3); i++) {
                        this.spawnTask.createEntity("Rabbit", pos.add(0.5, 1, 0.5));
                    }
                }
            }
        }
    }
}
