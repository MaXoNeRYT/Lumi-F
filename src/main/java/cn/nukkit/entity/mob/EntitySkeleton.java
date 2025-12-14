package cn.nukkit.entity.mob;

import cn.nukkit.Difficulty;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemNamespaceId;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector2;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.utils.Utils;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;

public class EntitySkeleton extends EntityWalkingMob implements EntitySmite {

    public static final int NETWORK_ID = 34;

    private boolean angryFlagSet;

    public EntitySkeleton(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.99f;
    }

    @Override
    public void attackEntity(Entity player) {
        if (!(player instanceof Player target)) return;
        if (this.attackDelay > 20 && this.distanceSquared(player) <= 100) {
            this.attackDelay = 0;

            double distance = this.distance(player);
            double gravity = 0.05;

            double power = 1.6;
            double dx = (target.x - this.x);
            double dz = (target.z - this.z);
            double dy = (target.y + target.getEyeHeight() - (this.y + this.getHeight() * 0.8));

            Vector2 motion = new Vector2(target.motionX, target.motionZ);
            dx += motion.x * 4.0;
            dz += motion.y * 4.0;

            double horizontal = Math.sqrt(dx * dx + dz * dz);

            double pitchCorrection = -Math.atan2(dy, horizontal) + (gravity * (horizontal / (power * power * 0.6)));

            double yaw = Math.toDegrees(Math.atan2(-dx, dz));
            double pitch = Math.toDegrees(pitchCorrection);

            this.yaw = (float) yaw;
            this.pitch = (float) pitch;
            this.updateMovement();

            Location pos = new Location(
                    this.x - Math.sin(Math.toRadians(this.yaw)) * 0.6,
                    this.y + this.getHeight() * 0.8,
                    this.z + Math.cos(Math.toRadians(this.yaw)) * 0.6,
                    (float) yaw,
                    (float) pitch,
                    this.level
            );

            EntityArrow arrow = (EntityArrow) Entity.createEntity("Arrow", pos, this);
            if (arrow == null) return;

            double motionX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
            double motionY = -Math.sin(Math.toRadians(pitch));
            double motionZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

            Vector2 spread = new Vector2(Utils.rand(-0.03f, 0.03f), Utils.rand(-0.03f, 0.03f));
            arrow.setMotion(new cn.nukkit.math.Vector3(
                    motionX + spread.x,
                    motionY,
                    motionZ + spread.y
            ).multiply(power));

            EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.BOW), arrow, power);
            this.server.getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                arrow.close();
                return;
            }

            ProjectileLaunchEvent launch = new ProjectileLaunchEvent(arrow);
            this.server.getPluginManager().callEvent(launch);
            if (launch.isCancelled()) {
                arrow.close();
                return;
            }

            arrow.spawnToAll();
            arrow.setPickupMode(EntityArrow.PICKUP_NONE);

            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BOW);
        }
    }


    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getId();
        pk.item = new ItemBow();
        pk.hotbarSlot = 0;
        player.dataPacket(pk);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {


        boolean hasUpdate = super.entityBaseTick(tickDiff);


        return hasUpdate;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        drops.add(Item.get(ItemNamespaceId.BONE, 0, Utils.rand(0, 2)));
        drops.add(Item.get(Item.ARROW, 0, Utils.rand(0, 2)));

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

    @Override
    public int nearbyDistanceMultiplier() {
        return 10;
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        boolean hasTarget = super.targetOption(creature, distance);
        if (hasTarget) {
            if (!this.angryFlagSet && creature != null) {
                this.setDataProperty(new LongEntityData(DATA_TARGET_EID, creature.getId()));
                this.angryFlagSet = true;
            }
        } else {
            if (this.angryFlagSet) {
                this.setDataProperty(new LongEntityData(DATA_TARGET_EID, 0));
                this.angryFlagSet = false;
                this.stayTime = 100;
            }
        }
        return hasTarget;
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        super.kill();

        if (this.lastDamageCause instanceof EntityDamageByChildEntityEvent) {
            Entity damager;
            if (((EntityDamageByChildEntityEvent) this.lastDamageCause).getChild() instanceof EntityArrow && (damager = ((EntityDamageByChildEntityEvent) this.lastDamageCause).getDamager()) instanceof Player) {

            }
        }
    }
}