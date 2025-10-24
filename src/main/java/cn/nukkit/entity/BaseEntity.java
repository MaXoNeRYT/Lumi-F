package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.mob.EntityFlyingMob;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.mob.EntityRavager;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.HeartParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.math3.util.FastMath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The base class of all entities that have an AI
 */
public abstract class BaseEntity extends EntityCreature implements EntityAgeable {

    private static final Map<String, Float> ARMOR_POINTS = new HashMap<>() {{
        put("minecraft:leather_helmet", 1f);
        put("minecraft:leather_chestplate", 3f);
        put("minecraft:leather_leggings", 2f);
        put("minecraft:leather_boots", 1f);

        put("minecraft:golden_helmet", 2f);
        put("minecraft:golden_chestplate", 5f);
        put("minecraft:golden_leggings", 3f);
        put("minecraft:golden_boots", 1f);

        put("minecraft:chainmail_helmet", 2f);
        put("minecraft:chainmail_chestplate", 5f);
        put("minecraft:chainmail_leggings", 4f);
        put("minecraft:chainmail_boots", 1f);

        put("minecraft:iron_helmet", 2f);
        put("minecraft:iron_chestplate", 6f);
        put("minecraft:iron_leggings", 5f);
        put("minecraft:iron_boots", 2f);

        put("minecraft:diamond_helmet", 3f);
        put("minecraft:diamond_chestplate", 8f);
        put("minecraft:diamond_leggings", 6f);
        put("minecraft:diamond_boots", 3f);

        put("minecraft:netherite_helmet", 3f);
        put("minecraft:netherite_chestplate", 8f);
        put("minecraft:netherite_leggings", 6f);
        put("minecraft:netherite_boots", 3f);

        //NOSENS

        put("minecraft:turtle_helmet", 2f);

        put("fireshaldrpg:leather_helmet", 1f);
        put("fireshaldrpg:leather_platebody", 3f);
        put("fireshaldrpg:leather_platelegs", 2f);
        put("fireshaldrpg:leather_boots", 1f);

        put("fireshaldrpg:copper_helm", 2f);
        put("fireshaldrpg:copper_platebody", 4f);
        put("fireshaldrpg:copper_platelegs", 3f);
        put("fireshaldrpg:copper_boots", 2f);

        put("fireshaldrpg:iron_helm", 3f);
        put("fireshaldrpg:iron_platebody", 5f);
        put("fireshaldrpg:iron_platelegs", 4f);
        put("fireshaldrpg:iron_boots", 3f);

        put("fireshaldrpg:black_helm", 3f);
        put("fireshaldrpg:black_platebody", 5f);
        put("fireshaldrpg:black_platelegs", 4f);
        put("fireshaldrpg:black_boots", 3f);

        put("fireshaldrpg:steel_helm", 4f);
        put("fireshaldrpg:steel_platebody", 6f);
        put("fireshaldrpg:steel_platelegs", 5f);
        put("fireshaldrpg:steel_boots", 4f);

        put("fireshaldrpg:mithril_helm", 5f);
        put("fireshaldrpg:mithril_platebody", 7f);
        put("fireshaldrpg:mithril_platelegs", 6f);
        put("fireshaldrpg:mithril_boots", 5f);

        put("fireshaldrpg:drake_bone_boots", 5f);
        put("fireshaldrpg:drake_bone_helm", 5f);
        put("fireshaldrpg:drake_bone_platebody", 7f);
        put("fireshaldrpg:drake_bone_platelegs", 6f);

        put("fireshaldrpg:adamant_helm", 6f);
        put("fireshaldrpg:adamant_platebody", 8f);
        put("fireshaldrpg:adamant_platelegs", 7f);
        put("fireshaldrpg:adamant_boots", 6f);

        put("fireshaldrpg:wrought_helm", 7f);
        put("fireshaldrpg:argent_platebody", 9f);
        put("fireshaldrpg:argent_platelegs", 8f);
        put("fireshaldrpg:argent_boots", 7f);

        put("fireshaldrpg:rune_helm", 7f);
        put("fireshaldrpg:rune_platebody", 9f);
        put("fireshaldrpg:rune_platelegs", 8f);
        put("fireshaldrpg:rune_boots", 7f);

    }};



    /**
     * Empty inventory
     * Used to fix the problem of getting the player's hand-held item null pointer
     */
    protected static PlayerInventory EMPTY_INVENTORY;

    public int stayTime = 0;
    protected int moveTime = 0;

    protected float moveMultiplier = 1.0f;

    protected Vector3 target = null;
    protected Entity followTarget = null;
    protected int attackDelay = 0;
    protected Player lastInteract;
    private short inLoveTicks = 0;
    private short inLoveCooldown = 0;

    private boolean baby = false;
    private boolean movement = true;
    private boolean friendly = false;

    public Item[] armor;


    public BaseEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (EMPTY_INVENTORY == null) {
            EMPTY_INVENTORY = new PlayerInventory(null);
        }

        this.setHealth(this.getMaxHealth());
        this.setAirTicks(400);
    }

    public abstract Vector3 updateMove(int tickDiff);

    public abstract int getKillExperience();

    public boolean isFriendly() {
        return this.friendly;
    }

    public boolean isMovement() {
        return this.movement;
    }

    public boolean isKnockback() {
        return this.knockBackTime > 0;
    }

    public void setFriendly(boolean bool) {
        this.friendly = bool;
    }

    public void setMovement(boolean value) {
        this.movement = value;
    }

    public double getSpeed() {
        if (this.baby) {
            return 1.2;
        }
        return 1;
    }

    public int getAge() {
        return this.age;
    }

    public Entity getTarget() {
        return this.followTarget != null ? this.followTarget : (this.target instanceof Entity ? (Entity) this.target : null);
    }

    public void setTarget(Entity target) {
        this.followTarget = target;
        this.moveTime = 0;
        this.stayTime = 0;
        this.target = null;
    }

    @Override
    public boolean isBaby() {
        return this.baby;
    }

    @Override
    public void setBaby(boolean baby) {
        this.baby = baby;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_BABY, baby);
        if (baby) {
            this.setScale(0.5f);
            this.age = Utils.rand(-2400, -1800);
        } else {
            this.setScale(1.0f);
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("Movement")) {
            this.setMovement(this.namedTag.getBoolean("Movement"));
        }

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if (this.namedTag.getBoolean("Baby")) {
            this.setBaby(true);
        }

        if (this.namedTag.contains("InLoveTicks")) {
            this.inLoveTicks = (short) this.namedTag.getShort("InLoveTicks");
        }

        if (this.namedTag.contains("InLoveCooldown")) {
            this.inLoveCooldown = (short) this.namedTag.getShort("InLoveCooldown");
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("Baby", this.baby);
        this.namedTag.putBoolean("Movement", this.isMovement());
        this.namedTag.putShort("Age", this.age);
        this.namedTag.putShort("InLoveTicks", this.inLoveTicks);
        this.namedTag.putShort("InLoveCooldown", this.inLoveCooldown);
    }

    public boolean targetOption(EntityCreature creature, double distance) {
        if (this instanceof EntityMob) {
            if (creature instanceof Player player) {
                return !player.closed && player.spawned && player.isAlive() && (player.isSurvival() || player.isAdventure()) && distance <= 100;
            }
            return creature.isAlive() && !creature.closed && distance <= 100;
        } else if (this instanceof EntityAnimal && this.isInLove()) {
            return creature instanceof BaseEntity && ((BaseEntity) creature).isInLove() && creature.isAlive() && !creature.closed && creature.getNetworkId() == this.getNetworkId() && distance <= 100;
        }
        return false;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        int ticksPerEntityDespawns = Server.getInstance().getSettings().world().entity().ticksPerEntityDespawns();
        if (this.canDespawn() &&
                this.age > ticksPerEntityDespawns &&
                !this.hasCustomName() &&
                !this.namedTag.getBoolean("nodespawn") &&
                !(this instanceof EntityBoss)) {
            this.close();
            return true;
        }

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this instanceof EntityMob && this.attackDelay < 200) {
            this.attackDelay++;
        }

        if (this.moveTime > 0) {
            this.moveTime -= tickDiff;
        }

        if (this.isBaby() && this.age > 0) {
            this.setBaby(false);
        }

        if (this.isInLove()) {
            this.inLoveTicks -= tickDiff;
            if (!this.isBaby() && this.age > 0 && this.age % 20 == 0) {
                for (int i = 0; i < 3; i++) {
                    this.level.addParticle(new HeartParticle(this.add(Utils.rand(-1.0, 1.0), this.getMountedYOffset() + Utils.rand(-1.0, 1.0), Utils.rand(-1.0, 1.0))));
                }
                Entity[] collidingEntities = this.level.getCollidingEntities(this.boundingBox.grow(0.5d, 0.5d, 0.5d));
                for (Entity entity : collidingEntities) {
                    if (this.checkSpawnBaby(entity)) {
                        break;
                    }
                }
            }
        } else if (this.isInLoveCooldown()) {
            this.inLoveCooldown -= tickDiff;
        }

        if (isDayBurning() && !this.closed && level.shouldMobBurn(this)) {
            if (this.armor == null || this.armor[0] == null || this.armor[0].getId() == 0) {
                this.setOnFire(100);
            } else if (this.armor[0].getId() == 0) {
                this.setOnFire(100);
            }
        }

        return hasUpdate;
    }

    public boolean isDayBurning() {
        return false;
    }

    protected boolean checkSpawnBaby(Entity entity) {
        if (!(entity instanceof BaseEntity baseEntity) || entity == this || entity.getNetworkId() != this.getNetworkId()) {
            return false;
        }
        if (!baseEntity.isInLove() || baseEntity.isBaby() || baseEntity.age <= 0) {
            return false;
        }

        Player player = baseEntity.lastInteract;
        baseEntity.lastInteract = null;

        this.setInLove(false);
        baseEntity.setInLove(false);

        this.setInLoveCooldown((short) 1200);
        baseEntity.setInLoveCooldown((short) 1200);

        this.stayTime = 60;
        baseEntity.stayTime = 60;

        int i = 0;
        for (Entity entity2 : this.chunk.getEntities().values()) {
            if (entity2.getNetworkId() == getNetworkId()) {
                i++;
                if (i > 10) {
                    return true;
                }
            }
        }

        BaseEntity baby = (BaseEntity) Entity.createEntity(getNetworkId(), this, new Object[0]);
        baby.setBaby(true);
        baby.spawnToAll();
        this.level.dropExpOrb(this, Utils.rand(1, 7));
        return true;
    }


    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isKnockback() && source instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) source).getDamager() instanceof Player) {
            return false;
        }

        if (this.fireProof && (source.getCause() == EntityDamageEvent.DamageCause.FIRE || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || source.getCause() == EntityDamageEvent.DamageCause.LAVA || source.getCause() == EntityDamageEvent.DamageCause.MAGMA)) {
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent) {
            if (this instanceof EntityRavager && Utils.rand()) {
                ((EntityDamageByEntityEvent) source).setKnockBack(0f);
            } else {
                ((EntityDamageByEntityEvent) source).setKnockBack(0.25f);
            }
        }

        super.attack(source);

        if (!source.isCancelled()) {
            this.target = null;
            this.stayTime = 0;
        }

        return true;
    }

    @Override
    public boolean move(double dx, double dy, double dz) {
        if (dy < -10 || dy > 10) {
            if (!(this instanceof EntityFlyingMob)) {
                this.kill();
            }
            return false;
        }

        if (dx == 0 && dz == 0 && dy == 0) {
            return false;
        }

        this.blocksAround = null;
        List<Entity> collidingEntities = List.of(this.level.getCollidingEntities(this.boundingBox.addCoord(dx, dy, dz)));

        for (Entity entity : collidingEntities) {
            if (entity instanceof EntityLiving && entity != this) {

                double edx = this.x - entity.x;
                double edz = this.z - entity.z;
                double distance = Math.max(0.01, Math.sqrt(edx * edx + edz * edz));

                double force = 0.15;
                dx += (edx / distance) * force;
                dz += (edz / distance) * force;
            }
        }

        double movX = dx * moveMultiplier;
        double movY = dy;
        double movZ = dz * moveMultiplier;

        AxisAlignedBB[] list = this.level.getCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

        for (AxisAlignedBB bb : list) {
            dx = bb.calculateXOffset(this.boundingBox, dx);
        }
        this.boundingBox.offset(dx, 0, 0);

        for (AxisAlignedBB bb : list) {
            dz = bb.calculateZOffset(this.boundingBox, dz);
        }
        this.boundingBox.offset(0, 0, dz);

        for (AxisAlignedBB bb : list) {
            dy = bb.calculateYOffset(this.boundingBox, dy);
        }
        this.boundingBox.offset(0, dy, 0);

        this.setComponents(this.x + dx, this.y + dy, this.z + dz);
        this.checkChunks();

        this.checkGroundState(movX, movY, movZ, dx, dy, dz);
        this.updateFallState(this.onGround);

        return true;
    }

    @Override
    protected boolean applyNameTag(Player player, Item nameTag) {
        String name = nameTag.getCustomName();

        if (!name.isEmpty()) {
            this.namedTag.putString("CustomName", name);
            this.namedTag.putBoolean("CustomNameVisible", true);
            this.setNameTag(name);
            this.setNameTagVisible(true);
            return true; // onInteract: true = decrease count
        }

        return false;
    }

    public void setInLove() {
        this.setInLove(true);
    }

    public void setInLove(boolean inLove) {
        if (inLove && !this.isBaby()) {
            this.inLoveTicks = 600;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE, true);
        } else {
            this.inLoveTicks = 0;
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE, false);
        }
    }

    public boolean isInLove() {
        return inLoveTicks > 0;
    }

    public void setInLoveCooldown(short inLoveCooldown) {
        this.inLoveCooldown = inLoveCooldown;
    }

    public boolean isInLoveCooldown() {
        return this.inLoveCooldown > 0;
    }

    /**
     * Check if the entity can swim in the block
     *
     * @param block block id
     * @return can swim
     */
    protected boolean canSwimIn(int block) {
        return block == BlockID.WATER || block == BlockID.STILL_WATER;
    }

    /**
     * Get a random set of armor
     *
     * @return armor items
     */
    public Item[] getRandomArmor() {
        Item[] slots = new Item[4];
        Item helmet = Item.get(0);
        Item chestplate = Item.get(0);
        Item leggings = Item.get(0);
        Item boots = Item.get(0);

        // Шлем
        switch (Utils.rand(1, 7)) {
            case 1 -> { // Leather
                if (Utils.rand(1, 100) < 40) {
                    helmet = Item.get("fireshaldrpg:leather_helmet", Utils.rand(30, 48), 1);
                }
            }
            case 2 -> { // Copper
                if (Utils.rand(1, 100) < 30) {
                    helmet = Item.get("fireshaldrpg:copper_helm", Utils.rand(50, 70), 1);
                }
            }
            case 3 -> { // Iron
                if (Utils.rand(1, 100) < 20) {
                    helmet = Item.get("fireshaldrpg:iron_helm", Utils.rand(100, 188), 1);
                }
            }
            case 4 -> { // Black
                if (Utils.rand(1, 100) < 15) {
                    helmet = Item.get("fireshaldrpg:black_helm", Utils.rand(120, 200), 1);
                }
            }
            case 5 -> { // Steel
                if (Utils.rand(1, 100) < 10) {
                    helmet = Item.get("fireshaldrpg:steel_helm", Utils.rand(150, 220), 1);
                }
            }
            case 6 -> { // Mithril
                if (Utils.rand(1, 100) < 5) {
                    helmet = Item.get("fireshaldrpg:mithril_helm", Utils.rand(180, 250), 1);
                }
            }
            case 7 -> { // Adamant
                if (Utils.rand(1, 100) < 2) {
                    helmet = Item.get("fireshaldrpg:adamant_helm", Utils.rand(200, 280), 1);
                }
            }
        }
        slots[0] = helmet;

        // Нагрудник
        if (Utils.rand(1, 4) != 1) {
            switch (Utils.rand(1, 7)) {
                case 1 -> {
                    if (Utils.rand(1, 100) < 40) {
                        chestplate = Item.get("fireshaldrpg:leather_platebody", Utils.rand(60, 73), 1);
                    }
                }
                case 2 -> {
                    if (Utils.rand(1, 100) < 30) {
                        chestplate = Item.get("fireshaldrpg:copper_platebody", Utils.rand(80, 120), 1);
                    }
                }
                case 3 -> {
                    if (Utils.rand(1, 100) < 20) {
                        chestplate = Item.get("fireshaldrpg:iron_platebody", Utils.rand(170, 233), 1);
                    }
                }
                case 4 -> {
                    if (Utils.rand(1, 100) < 15) {
                        chestplate = Item.get("fireshaldrpg:black_platebody", Utils.rand(180, 240), 1);
                    }
                }
                case 5 -> {
                    if (Utils.rand(1, 100) < 10) {
                        chestplate = Item.get("fireshaldrpg:steel_platebody", Utils.rand(200, 260), 1);
                    }
                }
                case 6 -> {
                    if (Utils.rand(1, 100) < 5) {
                        chestplate = Item.get("fireshaldrpg:mithril_platebody", Utils.rand(240, 300), 1);
                    }
                }
                case 7 -> {
                    if (Utils.rand(1, 100) < 2) {
                        chestplate = Item.get("fireshaldrpg:adamant_platebody", Utils.rand(300, 360), 1);
                    }
                }
            }
        }
        slots[1] = chestplate;

        // Поножи
        if (Utils.rand(1, 2) == 2) {
            switch (Utils.rand(1, 7)) {
                case 1 -> {
                    if (Utils.rand(1, 100) < 40) {
                        leggings = Item.get("fireshaldrpg:leather_platelegs", Utils.rand(35, 68), 1);
                    }
                }
                case 2 -> {
                    if (Utils.rand(1, 100) < 30) {
                        leggings = Item.get("fireshaldrpg:copper_platelegs", Utils.rand(60, 100), 1);
                    }
                }
                case 3 -> {
                    if (Utils.rand(1, 100) < 20) {
                        leggings = Item.get("fireshaldrpg:iron_platelegs", Utils.rand(170, 218), 1);
                    }
                }
                case 4 -> {
                    if (Utils.rand(1, 100) < 15) {
                        leggings = Item.get("fireshaldrpg:black_platelegs", Utils.rand(180, 230), 1);
                    }
                }
                case 5 -> {
                    if (Utils.rand(1, 100) < 10) {
                        leggings = Item.get("fireshaldrpg:steel_platelegs", Utils.rand(200, 250), 1);
                    }
                }
                case 6 -> {
                    if (Utils.rand(1, 100) < 5) {
                        leggings = Item.get("fireshaldrpg:mithril_platelegs", Utils.rand(240, 290), 1);
                    }
                }
                case 7 -> {
                    if (Utils.rand(1, 100) < 2) {
                        leggings = Item.get("fireshaldrpg:adamant_platelegs", Utils.rand(300, 350), 1);
                    }
                }
            }
        }
        slots[2] = leggings;

        // Ботинки
        if (Utils.rand(1, 5) < 3) {
            switch (Utils.rand(1, 7)) {
                case 1 -> {
                    if (Utils.rand(1, 100) < 40) {
                        boots = Item.get("fireshaldrpg:leather_boots", Utils.rand(35, 58), 1);
                    }
                }
                case 2 -> {
                    if (Utils.rand(1, 100) < 30) {
                        boots = Item.get("fireshaldrpg:copper_boots", Utils.rand(50, 86), 1);
                    }
                }
                case 3 -> {
                    if (Utils.rand(1, 100) < 20) {
                        boots = Item.get("fireshaldrpg:iron_boots", Utils.rand(100, 188), 1);
                    }
                }
                case 4 -> {
                    if (Utils.rand(1, 100) < 15) {
                        boots = Item.get("fireshaldrpg:black_boots", Utils.rand(120, 200), 1);
                    }
                }
                case 5 -> {
                    if (Utils.rand(1, 100) < 10) {
                        boots = Item.get("fireshaldrpg:steel_boots", Utils.rand(150, 220), 1);
                    }
                }
                case 6 -> {
                    if (Utils.rand(1, 100) < 5) {
                        boots = Item.get("fireshaldrpg:mithril_boots", Utils.rand(180, 250), 1);
                    }
                }
                case 7 -> {
                    if (Utils.rand(1, 100) < 2) {
                        boots = Item.get("fireshaldrpg:adamant_boots", Utils.rand(200, 280), 1);
                    }
                }
            }
        }
        slots[3] = boots;

        return slots;
    }


    /**
     * Increases mob's health according to armor the mob has (temporary workaround until armor damage modifiers are implemented for mobs)
     */
    protected void addArmorExtraHealth() {
        if (this.armor != null && this.armor.length == 4) {
            switch (armor[0].getId()) {
                case Item.LEATHER_CAP -> this.addHealth(1);
                case Item.GOLD_HELMET, Item.CHAIN_HELMET, Item.IRON_HELMET -> this.addHealth(2);
                case Item.DIAMOND_HELMET -> this.addHealth(3);
            }
            switch (armor[1].getId()) {
                case Item.LEATHER_TUNIC -> this.addHealth(2);
                case Item.GOLD_CHESTPLATE, Item.CHAIN_CHESTPLATE, Item.IRON_CHESTPLATE -> this.addHealth(3);
                case Item.DIAMOND_CHESTPLATE -> this.addHealth(4);
            }
            switch (armor[2].getId()) {
                case Item.LEATHER_PANTS -> this.addHealth(1);
                case Item.GOLD_LEGGINGS, Item.CHAIN_LEGGINGS, Item.IRON_LEGGINGS -> this.addHealth(2);
                case Item.DIAMOND_LEGGINGS -> this.addHealth(3);
            }
            switch (armor[3].getId()) {
                case Item.LEATHER_BOOTS -> this.addHealth(1);
                case Item.GOLD_BOOTS, Item.CHAIN_BOOTS, Item.IRON_BOOTS -> this.addHealth(2);
                case Item.DIAMOND_BOOTS -> this.addHealth(3);
            }
        }
    }

    /**
     * Increase the maximum health and health. Used for armored mobs.
     *
     * @param health amount of health to add
     */
    private void addHealth(int health) {
        boolean wasMaxHealth = this.getHealth() == this.getMaxHealth();
        this.setMaxHealth(this.getMaxHealth() + health);
        if (wasMaxHealth) {
            this.setHealth(this.getHealth() + health);
        }
    }

    /**
     * Check whether a mob is allowed to despawn
     *
     * @return can despawn
     */
    public boolean canDespawn() {
        return Server.getInstance().getSettings().world().entity().entityDespawnTask();
    }

    /**
     * How near a player the mob should get before it starts attacking
     *
     * @return distance
     */
    public int nearbyDistanceMultiplier() {
        return 1;
    }

    @Override
    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (onGround && movX == 0 && movY == 0 && movZ == 0 && dx == 0 && dy == 0 && dz == 0) {
            return;
        }
        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
        this.onGround = (movY != dy && movY < 0);
    }

    public static void setProjectileMotion(EntityProjectile projectile, double pitch, double yawR, double pitchR, double speed) {
        double verticalMultiplier = Math.cos(pitchR);
        double x = verticalMultiplier * Math.sin(-yawR);
        double z = verticalMultiplier * Math.cos(yawR);
        double y = Math.sin(-(FastMath.toRadians(pitch)));
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude > 0) {
            x += (x * (speed - magnitude)) / magnitude;
            y += (y * (speed - magnitude)) / magnitude;
            z += (z * (speed - magnitude)) / magnitude;
        }
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        x += rand.nextGaussian() * 0.007499999832361937 * 6;
        y += rand.nextGaussian() * 0.007499999832361937 * 6;
        z += rand.nextGaussian() * 0.007499999832361937 * 6;
        projectile.setMotion(new Vector3(x, y, z));
    }

    public boolean canTarget(Entity entity) {
        return entity instanceof Player && entity.canBeFollowed();
    }

    @Override
    protected void checkBlockCollision() {
        for (Block block : this.getCollisionBlocks()) {
            block.onEntityCollide(this);
        }

        // TODO: portals
    }

    /**
     * Get armor defense points for item
     *
     * @param item item id
     * @return defense points
     */
    protected float getArmorPoints(int item) {
        Float points = ARMOR_POINTS.get(item);
        if (points == null) {
            return 0;
        }
        return points;
    }

    /**
     * Play attack animation to viewers
     */
    protected void playAttack() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.ARM_SWING;
        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    /**
     * 满足攻击目标条件
     *
     * @return 是否满足
     */
    public boolean isMeetAttackConditions(Vector3 target) {
        return this.getServer().getSettings().world().entity().mobAi() && target instanceof Entity;
    }

    /**
     * 获取攻击目标
     *
     * @param target 目标
     * @return 有可能为空指针
     */
    protected Entity getAttackTarget(Vector3 target) {
        if (isMeetAttackConditions(target)) {
            Entity entity = (Entity) target;
            if (!entity.isClosed() && target != this.followTarget) {
                return entity;
            }
        }
        return null;
    }

    protected boolean isInTickingRange() {
        for (Player player : this.level.getPlayers().values()) {
            if (player.distanceSquared(this) < 6400) { // 80 blocks
                return true;
            }
        }
        return false;
    }
}
