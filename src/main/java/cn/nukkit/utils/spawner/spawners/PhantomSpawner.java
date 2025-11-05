package cn.nukkit.utils.spawner.spawners;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityPhantom;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.spawner.AbstractEntitySpawner;
import cn.nukkit.utils.spawner.EntitySpawnerTask;

public class PhantomSpawner extends AbstractEntitySpawner {

    public PhantomSpawner(EntitySpawnerTask spawnTask) {
        super(spawnTask, EntityPhantom.class, SpawnerType.MOB);
    }

    @Override
    public void spawn(Player player, Position pos, Level level) {
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);

        if (level.isMobSpawningAllowedByTime()) {
            if (pos.y < 130 && pos.y > 0 && biomeId != 14 && biomeId != 15 ) { // "Phantoms spawn if the player's Y-coordinate is between 1 and 129" - Minecraft Wiki
                EntityPhantom phantom = (EntityPhantom) this.spawnTask.createEntity("Phantom", pos);
                if (phantom != null) {
                    phantom.setTarget(player);
                }
            }
        }
    }
}
