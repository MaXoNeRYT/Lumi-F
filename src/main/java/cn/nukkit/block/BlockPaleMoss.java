package cn.nukkit.block;

import cn.nukkit.level.Position;

import java.util.Random;

public class BlockPaleMoss extends BlockMoss {
    public BlockPaleMoss() {
    }

    public BlockPaleMoss(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return PALE_MOSS_BLOCK;
    }

    @Override
    public String getName() {
        return "Pale Moss Block";
    }

    public boolean canBePopulated(Position pos) {
        return pos.add(0, -1, 0).getLevelBlock().isSolid() && pos.add(0, -1, 0).getLevelBlock().getId() != BlockID.PALE_MOSS_CARPET && pos.getLevelBlock().getId() == BlockID.AIR;
    }

    public boolean canBePopulated2BlockAir(Position pos) {
        return pos.add(0, -1, 0).getLevelBlock().isSolid() && pos.add(0, -1, 0).getLevelBlock().getId() != BlockID.PALE_MOSS_CARPET && pos.getLevelBlock().getId() == BlockID.AIR && pos.add(0, 1, 0).getLevelBlock().getId() == BlockID.AIR;
    }

    public void convertToMoss(Position pos) {
        Random random = new Random();
        for (double x = pos.x - 3; x <= pos.x + 3; x++) {
            for (double z = pos.z - 3; z <= pos.z + 3; z++) {
                for (double y = pos.y + 5; y >= pos.y - 5; y--) {
                    if (canConvertToMoss(pos.level.getBlock(new Position(x, y, z, pos.level))) && (random.nextDouble() < 0.6 || Math.abs(x - pos.x) < 3 && Math.abs(z - pos.z) < 3)) {
                        pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.PALE_MOSS_BLOCK));
                        break;
                    }
                }
            }
        }
    }

    public void populateRegion(Position pos) {
        Random random = new Random();
        for (double x = pos.x - 3; x <= pos.x + 3; x++) {
            for (double z = pos.z - 3; z <= pos.z + 3; z++) {
                for (double y = pos.y + 5; y >= pos.y - 5; y--) {
                    if (canBePopulated(new Position(x, y, z, pos.level))) {
                        if (!canGrowPlant(new Position(x, y, z, pos.level)))
                            break;
                        double randomDouble = random.nextDouble();
                        if (randomDouble >= 0 && randomDouble < 0.3125) {
                            pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.TALL_GRASS), true, true);
                        }
                        if (randomDouble >= 0.3125 && randomDouble < 0.46875) {
                            pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.PALE_MOSS_CARPET), true, true);
                        }
                        if (randomDouble >= 0.46875 && randomDouble < 0.53125) {
                            //TODO: double plants
                            /*if (canBePopulated2BlockAir(new Position(x, y, z, pos.level))) {
                                BlockDoublePlant rootBlock = (BlockDoublePlant) Block.get(BlockID.DOUBLE_PLANT);
                                rootBlock.place()
                                pos.level.setBlock(new Position(x, y, z, pos.level), rootBlock,true,true);
                                BlockDoublePlant topBlock = (BlockDoublePlant) Block.get(BlockID.DOUBLE_PLANT);
                                topBlock.setDoublePlantType(DoublePlantType.FERN);
                                topBlock.setTopHalf(true);
                                pos.level.setBlock(new Position(x, y+1, z, pos.level), topBlock,true,true);
                            }else {
                                BlockTallGrass block = (BlockTallGrass) Block.get(BlockID.TALL_GRASS);
                                block.setPropertyValue(BlockTallGrass.TALL_GRASS_TYPE, TallGrassType.TALL);
                                pos.level.setBlock(new Position(x, y, z, pos.level), block,true,true);
                            }*/
                        }
                        if (randomDouble >= 0.6 && randomDouble < 1) {
                            pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.AIR), true, true);
                        }
                        break;
                    }
                }
            }
        }
    }
}
