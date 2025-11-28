package cn.nukkit.item.enchantment.custom;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.List;

@FunctionalInterface
public interface CustomEnchantmentDisplay {
    void apply(Item item, List<Enchantment> enchantments);
}
