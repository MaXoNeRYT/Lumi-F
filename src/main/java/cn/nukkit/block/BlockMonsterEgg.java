package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntitySilverfish;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.Utils;

public class BlockMonsterEgg extends BlockSolidMeta {

    public static final int STONE = 0;
    public static final int COBBLESTONE = 1;
    public static final int STONE_BRICK = 2;
    public static final int MOSSY_BRICK = 3;
    public static final int CRACKED_BRICK = 4;
    public static final int CHISELED_BRICK = 5;

    private static final String[] NAMES = new String[]{
            "Stone",
            "Cobblestone",
            "Stone Brick",
            "Mossy Stone Brick",
            "Cracked Stone Brick",
            "Chiseled Stone Brick"
    };

    public BlockMonsterEgg() {
        this(0);
    }

    public BlockMonsterEgg(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MONSTER_EGG;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 5 ? 0 : this.getDamage()] + " Monster Egg";
    }

    @Override
    public Item[] getDrops(Item item) {
        switch (this.getDamage()) {
            case STONE: // заражённый камень
                // Дропаем заражённый булыжник
                if (item != null && item.isPickaxe()) {
                    return new Item[]{Item.get(Block.MONSTER_EGG, COBBLESTONE, 1)};
                }

            case COBBLESTONE: // заражённый булыжник
                // Только если ломается киркой
                if (item != null && item.isPickaxe()) {
                    return new Item[]{Item.get(Block.MONSTER_EGG, COBBLESTONE, 1)};
                }
                return Item.EMPTY_ARRAY;

            default:
                return Item.EMPTY_ARRAY;
        }
    }



}
