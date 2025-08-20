package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBoneMeal;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.block.data.BlockColor;

import java.util.Random;

public class BlockMoss extends BlockDirt {

    public BlockMoss() {
    }

    public BlockMoss(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return MOSS_BLOCK;
    }

    @Override
    public String getName() {
        return "Moss Block";
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item instanceof ItemBoneMeal) {
            this.convertToMoss(this);
            this.populateRegion(this);
            this.level.addParticle(new BoneMealParticle(this));
            item.count--;
            return true;
        }
        return false;
    }

    public boolean canConvertToMoss(Block block) {
        int id = block.getId();
        return id == BlockID.GRASS ||
                id == BlockID.DIRT ||
                id == BlockID.DIRT_WITH_ROOTS ||
                id == BlockID.STONE ||
                id == BlockID.MYCELIUM ||
                id == BlockID.DEEPSLATE ||
                id == BlockID.TUFF;

    }

    public boolean canBePopulated(Position pos) {
        return pos.add(0, -1, 0).getLevelBlock().isSolid() && pos.add(0, -1, 0).getLevelBlock().getId() != BlockID.MOSS_CARPET && pos.getLevelBlock().getId() == BlockID.AIR;
    }

    public boolean canBePopulated2BlockAir(Position pos) {
        return pos.add(0, -1, 0).getLevelBlock().isSolid() && pos.add(0, -1, 0).getLevelBlock().getId() != BlockID.MOSS_CARPET && pos.getLevelBlock().getId() == BlockID.AIR && pos.add(0, 1, 0).getLevelBlock().getId() == BlockID.AIR;
    }

    public void convertToMoss(Position pos) {
        Random random = new Random();
        for (double x = pos.x - 3; x <= pos.x + 3; x++) {
            for (double z = pos.z - 3; z <= pos.z + 3; z++) {
                for (double y = pos.y + 5; y >= pos.y - 5; y--) {
                    if (canConvertToMoss(pos.level.getBlock(new Position(x, y, z, pos.level))) && (random.nextDouble() < 0.6 || Math.abs(x - pos.x) < 3 && Math.abs(z - pos.z) < 3)) {
                        pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.MOSS_BLOCK));
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
                            pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.MOSS_CARPET), true, true);
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
                        if (randomDouble >= 0.53125 && randomDouble < 0.575) {
                            pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.AZALEA), true, true);
                        }
                        if (randomDouble >= 0.575 && randomDouble < 0.6) {
                            pos.level.setBlock(new Position(x, y, z, pos.level), Block.get(BlockID.FLOWERING_AZALEA), true, true);
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

    public boolean canGrowPlant(Position pos) {
        switch (pos.add(0, -1, 0).getLevelBlock().getId()) {
            case GRASS:
            case DIRT:
            case PODZOL:
            case FARMLAND:
            case MYCELIUM:
            case DIRT_WITH_ROOTS:
            case MOSS_BLOCK:
                return true;
            default:
                return false;
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getFullId() {
        return this.getId() << Block.DATA_BITS;
    }

    @Override
    public void setDamage(int meta) {
        // Noop
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.canHarvestWithHand() || this.canHarvest(item)) {
            return new Item[]{this.toItem()};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }
}
