package cn.nukkit.dispenser.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class PotionDispenseBehaviour extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        if (item.getDamage() == PotionType.WATER.id()) {
            Block target = block.getSide(face);

            if (target instanceof BlockDirt) {
                if (target.onActivate(item, null)) {
                    return Item.get(Item.GLASS_BOTTLE);
                }
                return item;
            }
        }

        return super.dispense(block, face, item);
    }
}