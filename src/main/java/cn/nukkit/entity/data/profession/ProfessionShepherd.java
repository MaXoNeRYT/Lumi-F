package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.data.DyeColor;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.RecipeBuildUtils;

import java.util.Random;

public class ProfessionShepherd extends Profession {

    public ProfessionShepherd() {
        super(3, BlockID.LOOM, "entity.villager.shepherd");
    }

    @Override
    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");
        Random random = new Random(seed);

        Item rod = Item.get(Item.FISHING_ROD);
        Enchantment rodEnchantment = Enchantment.get(new int[]{Enchantment.ID_DURABILITY, Enchantment.ID_LURE, Enchantment.ID_FORTUNE_FISHING}[random.nextInt(2)]);
        rodEnchantment.setLevel(random.nextInt(3) + 1);
        rod.addEnchantment(rodEnchantment);

        Item dye1 = ItemDye.getByColor(DyeColor.getByDyeData(random.nextInt(5)));
        dye1.setCount(12);

        Item dye2 = ItemDye.getByColor(DyeColor.getByDyeData(5 + random.nextInt(5)));
        dye2.setCount(12);

        Item dye3 = ItemDye.getByColor(DyeColor.getByDyeData(10 + random.nextInt(6)));
        dye3.setCount(12);

        recipes.add(RecipeBuildUtils.of(Item.get(Item.WOOL, random.nextInt(16), 18), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.SHEARS))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(dye1, Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.WOOL))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.CARPET))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(dye2, Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.BED, DyeColor.BLUE.getWoolData()))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(dye3, Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.BANNER))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.PAINTING))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build());
        return recipes;
    }

}
