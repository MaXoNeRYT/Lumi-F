package cn.nukkit.item.enchantment.custom;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.StringJoiner;

public final class CustomEnchantmentDisplayStandard implements CustomEnchantmentDisplay {

    @Override
    public void apply(Item item, List<Enchantment> customEnchantments) {
        CompoundTag tag = !item.hasCompoundTag() ? new CompoundTag() : item.getNamedTag();

        if (!customEnchantments.isEmpty()) {
            var joiner = new StringJoiner("\n", String.valueOf(TextFormat.RESET) + TextFormat.AQUA + item.getName() + "\n", "");
            for (var enchantment : customEnchantments) {
                joiner.add(TextFormat.GRAY + enchantment.getName() + " " + Enchantment.getLevelString(enchantment.getLevel()));
            }

            var customName = joiner.toString();
            if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
                tag.getCompound("display").putString("Name", customName);
            } else {
                tag.putCompound("display", new CompoundTag()
                        .putString("Name", customName)
                );
            }
        } else {
            tag.remove("display");
        }

        item.setNamedTag(tag);
    }
}
