package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityWitch;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class WitchSpawner extends AbstractEntitySpawner {

    public WitchSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);
        if (Utils.rand(1, 5) == 1 && biomeId != 6 && biomeId != 134) {
            return;
        }
        if (level.getBlockLightAt((int) pos.x, (int) pos.y + 1, (int) pos.z) == 0) {
            if (level.isMobSpawningAllowedByTime()) {
                this.spawnTask.createEntity("Witch", pos.add(0.5, 1, 0.5));
            }
        }
    }

    @Override
    public final int getEntityNetworkId() {
        return EntityWitch.NETWORK_ID;
    }
}
