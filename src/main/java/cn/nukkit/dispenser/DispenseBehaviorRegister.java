package cn.nukkit.dispenser;

import cn.nukkit.block.material.BlockType;
import cn.nukkit.block.material.tags.BlockInternalTags;
import cn.nukkit.block.material.tags.BlockTag;
import cn.nukkit.dispenser.impl.*;
import cn.nukkit.item.ItemNamespaceId;
import cn.nukkit.item.material.ItemType;
import cn.nukkit.item.material.tags.ItemTag;
import cn.nukkit.item.material.tags.ItemTags;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CreeperFace
 */
public final class DispenseBehaviorRegister {

    private static final Map<String, DispenseBehavior> BEHAVIORS = new HashMap<>();
    private static final DispenseBehavior DEFAULT_BEHAVIOR = new DefaultDispenseBehavior();

    public static DispenseBehavior getBehavior(String id) {
        return BEHAVIORS.getOrDefault(id, DEFAULT_BEHAVIOR);
    }

    public static void registerBehavior(String itemId, DispenseBehavior behavior) {
        BEHAVIORS.put(itemId, behavior);
    }

    public static void registerBehaviorByItemTag(ItemTag tag, DispenseBehavior behavior) {
        for (ItemType type : tag.getItemTypes()) {
            registerBehavior(type.getIdentifier(), behavior);
        }
    }

    public static void registerBehaviorByBlockTag(BlockTag tag, DispenseBehavior behavior) {
        for (BlockType type : tag.getBlockTypes()) {
            registerBehavior(type.getIdentifier(), behavior);
        }
    }

    public static void removeDispenseBehavior(String id) {
        BEHAVIORS.remove(id);
    }

    public static void init() {
        registerBehavior(ItemNamespaceId.OAK_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.SPRUCE_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.BIRCH_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.JUNGLE_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.ACACIA_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.DARK_OAK_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.MANGROVE_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.CHERRY_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.PALE_OAK_BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.BAMBOO_RAFT, new BoatDispenseBehavior());

        registerBehavior(ItemNamespaceId.BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.MILK_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.WATER_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.LAVA_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.COD_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.SALMON_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.TROPICAL_FISH_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.PUFFERFISH_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.AXOLOTL_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.POWDER_SNOW_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.TADPOLE_BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemNamespaceId.BONE_MEAL, new BoneMealDispenseBehavior());

        registerBehaviorByItemTag(ItemTags.SPAWN_EGG, new SpawnEggDispenseBehavior());
        registerBehavior(ItemNamespaceId.FIREWORK_ROCKET, new FireworksDispenseBehavior());
        registerBehavior(ItemNamespaceId.FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());
        registerBehaviorByBlockTag(BlockInternalTags.DYED_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemNamespaceId.UNDYED_SHULKER_BOX, new UndyedShulkerBoxDispenseBehavior());

        registerBehavior(ItemNamespaceId.TNT, new TNTDispenseBehavior());
        registerBehavior(ItemNamespaceId.FIRE_CHARGE, new FireChargeDispenseBehavior());
        registerBehavior(ItemNamespaceId.SHEARS, new ShearsDispenseBehaviour());
        registerBehavior(ItemNamespaceId.POTION, new PotionDispenseBehaviour());
        registerBehavior(ItemNamespaceId.ARROW, new ArrowDispenseBehavior());
        registerBehavior(ItemNamespaceId.EGG, new EggDispenseBehavior());
        registerBehavior(ItemNamespaceId.SNOWBALL, new SnowballDispenseBehavior());
        registerBehavior(ItemNamespaceId.EXPERIENCE_BOTTLE, new ThrownExpBottleDispenseBehavior());
        registerBehavior(ItemNamespaceId.SPLASH_POTION, new ThrownPotionDispenseBehavior());
        registerBehavior(ItemNamespaceId.LINGERING_POTION, new LingeringPotionDispenseBehavior());
        registerBehavior(ItemNamespaceId.TRIDENT, new ThrownPotionDispenseBehavior());

        registerBehavior(ItemNamespaceId.ACACIA_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.DARK_OAK_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.BIRCH_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.JUNGLE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.MANGROVE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.SPRUCE_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.OAK_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.CHERRY_CHEST_BOAT, new ChestBoatDispenseBehavior());
        registerBehavior(ItemNamespaceId.BAMBOO_CHEST_RAFT, new ChestBoatDispenseBehavior());

        registerBehavior(ItemNamespaceId.MINECART, new MinecartDispenseBehavior());
        registerBehavior(ItemNamespaceId.CHEST_MINECART, new MinecartDispenseBehavior());
        registerBehavior(ItemNamespaceId.HOPPER_MINECART, new MinecartDispenseBehavior());
        registerBehavior(ItemNamespaceId.TNT_MINECART, new MinecartDispenseBehavior());
    }
}
