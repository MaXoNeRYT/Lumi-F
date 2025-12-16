package cn.nukkit.level.format.leveldb.updater;

import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_21_110 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_21_110();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 21, 60, false, false).match("name", "minecraft:chain")
                .edit("name", helper -> helper.replaceWith("name", "minecraft:iron_chain"));

        ctx.addUpdater(1, 21, 60, false, false)
                .match("name", "minecraft:lightning_rod")
                .visit("states")
                .tryAdd("powered_bit", (byte) 0);

    }
}