package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.passive.EntityOcelot;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class OcelotSpawner extends AbstractEntitySpawner {

    public OcelotSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) != 1) {
            return;
        }
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
        if (biomeId == 21 || biomeId == 149 || biomeId == 23 || biomeId == 151 || biomeId == 48 || biomeId == 49) {
            final int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
            if (blockId == Block.GRASS) {
                if (level.isAnimalSpawningAllowedByTime()) {
                    for (int i = 0; i < Utils.rand(1, 2); i++) {
                        BaseEntity entity = this.spawnTask.createEntity("Ocelot", pos.add(0.5, 1, 0.5));
                        if (entity == null) return;
                        if (Utils.rand(1, 20) == 1) {
                            entity.setBaby(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return EntityOcelot.NETWORK_ID;
    }
}
