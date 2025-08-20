package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityStray;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;

public class StraySpawner extends AbstractEntitySpawner {

    public StraySpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
        if (biomeId == 12 || biomeId == 30 || biomeId == 140 || biomeId == 10 || biomeId == 46 || biomeId == 47) {
            if (level.getBlockLightAt((int) pos.x, (int) pos.y + 1, (int) pos.z) == 0) {
                if (level.isMobSpawningAllowedByTime()) {
                    this.spawnTask.createEntity("Stray", pos.add(0.5, 1, 0.5));
                }
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return EntityStray.NETWORK_ID;
    }
}
