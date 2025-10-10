package cn.nukkit.recipe.impl.special;

import cn.nukkit.Player;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.impl.MultiRecipe;
import cn.nukkit.item.Item;

import java.util.Collection;

/**
 * @author glorydark
 * @date {2024/1/8} {16:04}
 */
public class RepairItemRecipe extends MultiRecipe {

    public RepairItemRecipe(){
        super(TYPE_REPAIR_ITEM);
    }

    @Override
    public boolean canExecute(Player player, Item outputItem, Collection<ItemDescriptor> inputs) {
        // Processing the checks about the inputs and outputItem
        ItemDescriptor[] items = inputs.toArray(new ItemDescriptor[0]);
        if (inputs.size() == 2 && ((DefaultDescriptor) items[0]).getItem() instanceof Item item1 && ((DefaultDescriptor) items[1]).getItem() instanceof Item item2) {
            if (item1.equals(item2, false, false)) {
                if (item1.isTool() || item1.isArmor()) {
                    int damage = calculateDamage(item1.getDamage(), item2.getDamage(), item1.getMaxDurability());
                    if (outputItem.equals(item1, false, true)) {
                        return outputItem.getDamage() == damage;
                    }
                }
            }
        } else if (inputs.size() == 1 && ((DefaultDescriptor) items[0]).getItem() instanceof Item item) {
            if (item.getCount() == 2) {
                if (item.isTool() || item.isArmor()) {
                    int damage = calculateDamage(item.getDamage(), item.getDamage(), item.getMaxDurability());
                    if (outputItem.equals(item, false, true)) {
                        return outputItem.getDamage() == damage;
                    }
                }
            }
        }
        return false;
    }

    public int calculateDamage(int d1, int d2, int maxDurability) {
        int j = maxDurability - d1;
        int k = maxDurability - d2;
        int l = j + k + maxDurability * 5 / 100;
        int i1 = maxDurability + 1 - l;
        if (i1 < 0) {
            i1 = 0;
        }
        return i1;
    }
}
