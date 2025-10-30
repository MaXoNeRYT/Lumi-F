package cn.nukkit.level.biome.impl.forest;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ForestBiome extends GrassyBiome {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_BIRCH = 1;
    public static final int TYPE_BIRCH_TALL = 2;

    public final int type;

    public ForestBiome() {
        this(TYPE_NORMAL);
    }

    public ForestBiome(int type) {
        super();
        this.type = type;

        if (type == TYPE_NORMAL) {
            PopulatorTree oakTrees = new PopulatorTree(BlockSapling.OAK);
            oakTrees.setBaseAmount(14);
            this.addPopulator(oakTrees);
        }

        if (type == TYPE_BIRCH || type == TYPE_BIRCH_TALL) {
            PopulatorTree birchTrees = new PopulatorTree(
                    type == TYPE_BIRCH_TALL ? BlockSapling.BIRCH_TALL : BlockSapling.BIRCH
            );
            birchTrees.setBaseAmount(10);
            this.addPopulator(birchTrees);
        }

        if (!(this instanceof FlowerForestBiome)) {
            PopulatorFlower flower = new PopulatorFlower();
            flower.setRandomAmount(3);
            flower.addType(DANDELION, 0);
            flower.addType(POPPY, 0);
            flower.addType(LILY_OF_THE_VALLEY, 0);
            flower.addType(DOUBLE_PLANT, BlockDoublePlant.LILAC);
            flower.addType(DOUBLE_PLANT, BlockDoublePlant.ROSE_BUSH);
            flower.addType(DOUBLE_PLANT, BlockDoublePlant.PEONY);
            this.addPopulator(flower);
        }
    }

    @Override
    public String getName() {
        switch (this.type) {
            case TYPE_BIRCH:
                return "Birch Forest";
            case TYPE_BIRCH_TALL:
                return "Birch Forest M";
            default:
                return "Forest";
        }
    }
}