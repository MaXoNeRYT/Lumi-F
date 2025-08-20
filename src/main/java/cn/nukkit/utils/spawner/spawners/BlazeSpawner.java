package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;
import cn.nukkit.utils.Utils;

public class BlazeSpawner extends AbstractEntitySpawner {

    public BlazeSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        if (Utils.rand(1, 3) == 1) {
            return;
        }

        this.spawnTask.createEntity("Blaze", pos);
    }

    @Override
    public int getEntityNetworkId() {
        return EntityBlaze.NETWORK_ID;
    }
}
