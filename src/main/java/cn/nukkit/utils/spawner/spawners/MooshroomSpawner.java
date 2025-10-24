package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.passive.EntityMooshroom;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class MooshroomSpawner extends AbstractEntitySpawner {

    public MooshroomSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityMooshroom.class, SpawnerType.ANIMAL);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(0, 3) == 1) {
            return;
        }
        int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
        if (biomeId == 14 || biomeId == 15) {
            if (level.isAnimalSpawningAllowedByTime()) {
                if (level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) == Block.MYCELIUM) {
                    for (int i = 0; i < Utils.rand(4, 8); i++) {
                        BaseEntity entity = this.spawnTask.createEntity("Mooshroom", pos.add(0.5, 1, 0.5));
                        if (entity == null) return;
                        if (Utils.rand(1, 20) == 1) {
                            entity.setBaby(true);
                        }
                    }
                }
            }
        }
    }
}
