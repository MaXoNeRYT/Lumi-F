package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.passive.EntityDonkey;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class DonkeySpawner extends AbstractEntitySpawner {

    public DonkeySpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityDonkey.class, SpawnerType.ANIMAL);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        if (blockId == Block.GRASS || blockId == Block.SNOW_LAYER) {
            final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
            if (biomeId == 1 || biomeId == 35 || biomeId == 128 || biomeId == 129) {
                if (level.isAnimalSpawningAllowedByTime()) {
                    for (int i = 0; i < Utils.rand(1, 3); i++) {
                        BaseEntity entity = this.spawnTask.createEntity("Donkey", pos.add(0.5, 1, 0.5));
                        if (entity == null) return;
                        if (Utils.rand(1, 20) == 1) {
                            entity.setBaby(true);
                        }
                        Level.RANDOM_TICK_BLOCKS.add(Block.BEEHIVE);
                    }
                }
            }
        }
    }


    @Override
    public boolean isWaterMob() {
        return true;
    }
}
