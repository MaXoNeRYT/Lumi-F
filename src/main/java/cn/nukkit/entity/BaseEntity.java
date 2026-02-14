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

    private long lastPlayerCheckTime = 0;
    private boolean cachedInTickingRange = false;

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
        return this.baby ? 1.2 : 1;
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

        if (this.namedTag.getBoolean("PlayerBred") && this.age % 100 == 0) {
            int playerBredCount = 0;
            for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(100, 100, 100))) {
                if (entity instanceof BaseEntity && ((BaseEntity) entity).namedTag.getBoolean("PlayerBred")) {
                    playerBredCount++;
                    if (playerBredCount > 50) {
                        this.close();
                        return true;
                    }
                }
            }
        }

        if (this.canDespawn() &&
                this.age > ticksPerEntityDespawns &&
                !this.hasCustomName() &&
                !this.namedTag.getBoolean("nodespawn") &&
                !this.namedTag.getBoolean("PlayerBred") &&
                !(this instanceof EntityBoss)) {
            this.close();
            return true;
        }

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this instanceof EntityMob && this.attackDelay < 200) {
            this.attackDelay++;
        }

        this.moveTime = Math.max(0, this.moveTime - tickDiff);

        if (this.isBaby() && this.age > 0) {
            this.setBaby(false);
        }

        if (this.isInLove()) {
            this.inLoveTicks -= tickDiff;
            if (!this.isBaby() && this.age > 0 && this.age % 20 == 0) {
                Vector3 basePos = this.add(0, this.getMountedYOffset(), 0);
                for (int i = 0; i < 3; i++) {
                    this.level.addParticle(new HeartParticle(basePos.add(
                            Utils.rand(-1.0, 1.0),
                            Utils.rand(-1.0, 1.0),
                            Utils.rand(-1.0, 1.0)
                    )));
                }

                Entity[] collidingEntities = this.level.getCollidingEntities(this.boundingBox.grow(0.5d, 0.5d, 0.5d));
                for (Entity entity : collidingEntities) {
                    if (this.checkSpawnBaby(entity)) {
                        break;
                    }
                }
            }
        } else if (this.inLoveCooldown > 0) {
            this.inLoveCooldown -= tickDiff;
        }

        if (isDayBurning() && !this.closed && level.shouldMobBurn(this)) {
            boolean shouldBurn = this.armor == null || this.armor.length == 0 ||
                    this.armor[0] == null || this.armor[0].getId() == 0;
            if (shouldBurn) {
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

        int count = 0;
        for (Entity entity2 : this.chunk.getEntities().values()) {
            if (entity2.getNetworkId() == getNetworkId()) {
                if (++count > 10) {
                    return true;
                }
            }
        }

        BaseEntity baby = (BaseEntity) Entity.createEntity(getNetworkId(), this, new Object[0]);
        baby.setBaby(true);
        baby.namedTag.putBoolean("PlayerBred", true);
        baby.spawnToAll();
        this.level.dropExpOrb(this, Utils.rand(1, 7));
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isKnockback() && source instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) source).getDamager() instanceof Player) {
            return false;
        }

        if (this.fireProof && (source.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                source.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                source.getCause() == EntityDamageEvent.DamageCause.MAGMA)) {
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
        if (!(this instanceof EntityFlyingMob) && (dy < -10 || dy > 10)) {
            this.kill();
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
                double distSq = edx * edx + edz * edz;

                if (distSq > 0.0001) {
                    double distance = Math.sqrt(distSq);
                    double force = 0.15 / distance;
                    dx += edx * force;
                    dz += edz * force;
                }
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
            return true;
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

        slots[0] = generateArmorPiece(1, 7, new String[]{
                "fireshaldrpg:leather_helmet",
                "fireshaldrpg:copper_helm",
                "fireshaldrpg:iron_helm",
                "fireshaldrpg:black_helm",
                "fireshaldrpg:steel_helm",
                "fireshaldrpg:mithril_helm",
                "fireshaldrpg:adamant_helm"
        }, new int[][]{
                {40, 30, 48},
                {30, 50, 70},
                {20, 100, 188},
                {15, 120, 200},
                {10, 150, 220},
                {5, 180, 250},
                {2, 200, 280}
        });

        if (Utils.rand(1, 4) != 1) {
            slots[1] = generateArmorPiece(1, 7, new String[]{
                    "fireshaldrpg:leather_platebody",
                    "fireshaldrpg:copper_platebody",
                    "fireshaldrpg:iron_platebody",
                    "fireshaldrpg:black_platebody",
                    "fireshaldrpg:steel_platebody",
                    "fireshaldrpg:mithril_platebody",
                    "fireshaldrpg:adamant_platebody"
            }, new int[][]{
                    {40, 60, 73},
                    {30, 80, 120},
                    {20, 170, 233},
                    {15, 180, 240},
                    {10, 200, 260},
                    {5, 240, 300},
                    {2, 300, 360}
            });
        } else {
            slots[1] = Item.get(0);
        }

        if (Utils.rand(1, 2) == 2) {
            slots[2] = generateArmorPiece(1, 7, new String[]{
                    "fireshaldrpg:leather_platelegs",
                    "fireshaldrpg:copper_platelegs",
                    "fireshaldrpg:iron_platelegs",
                    "fireshaldrpg:black_platelegs",
                    "fireshaldrpg:steel_platelegs",
                    "fireshaldrpg:mithril_platelegs",
                    "fireshaldrpg:adamant_platelegs"
            }, new int[][]{
                    {40, 35, 68},
                    {30, 60, 100},
                    {20, 170, 218},
                    {15, 180, 230},
                    {10, 200, 250},
                    {5, 240, 290},
                    {2, 300, 350}
            });
        } else {
            slots[2] = Item.get(0);
        }

        if (Utils.rand(1, 5) < 3) {
            slots[3] = generateArmorPiece(1, 7, new String[]{
                    "fireshaldrpg:leather_boots",
                    "fireshaldrpg:copper_boots",
                    "fireshaldrpg:iron_boots",
                    "fireshaldrpg:black_boots",
                    "fireshaldrpg:steel_boots",
                    "fireshaldrpg:mithril_boots",
                    "fireshaldrpg:adamant_boots"
            }, new int[][]{
                    {40, 35, 58},
                    {30, 50, 86},
                    {20, 100, 188},
                    {15, 120, 200},
                    {10, 150, 220},
                    {5, 180, 250},
                    {2, 200, 280}
            });
        } else {
            slots[3] = Item.get(0);
        }

        return slots;
    }

    private Item generateArmorPiece(int minTier, int maxTier, String[] types, int[][] chances) {
        int tier = Utils.rand(minTier, maxTier);
        int index = tier - 1;

        if (Utils.rand(1, 100) < chances[index][0]) {
            return Item.get(types[index], Utils.rand(chances[index][1], chances[index][2]), 1);
        }

        return Item.get(0);
    }

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
            double factor = (speed - magnitude) / magnitude;
            x += x * factor;
            y += y * factor;
            z += z * factor;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double randomFactor = 0.007499999832361937 * 6;
        x += rand.nextGaussian() * randomFactor;
        y += rand.nextGaussian() * randomFactor;
        z += rand.nextGaussian() * randomFactor;

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
    }

    /**
     * Get armor defense points for item
     *
     * @param item item id
     * @return defense points
     */
    protected float getArmorPoints(int item) {
        Float points = ARMOR_POINTS.get(item);
        return points == null ? 0 : points;
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
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPlayerCheckTime < 100) {
            return cachedInTickingRange;
        }

        lastPlayerCheckTime = currentTime;

        for (Player player : this.level.getPlayers().values()) {
            if (player.distanceSquared(this) < 6400) { // 80 blocks
                cachedInTickingRange = true;
                return true;
            }
        }

        cachedInTickingRange = false;
        return false;
    }
}