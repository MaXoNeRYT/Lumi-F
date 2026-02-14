package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.apache.commons.math3.util.FastMath;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EntityWalkingAnimal extends BaseEntity implements EntityAnimal {

    private static final ExecutorService AI_EXECUTOR = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "AnimalAI-Worker-" + threadNumber.getAndIncrement());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                }
            }
    );

    private static final ScheduledExecutorService AI_SCHEDULER = Executors.newScheduledThreadPool(
            1,
            r -> {
                Thread thread = new Thread(r, "AnimalAI-Scheduler");
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }
    );

    private static final ConcurrentLinkedQueue<EntityWalkingAnimal> aiQueue = new ConcurrentLinkedQueue<>();

    private static final ConcurrentHashMap<EntityWalkingAnimal, Future<?>> pendingAiTasks = new ConcurrentHashMap<>();

    private static volatile boolean initialized = false;
    private static volatile boolean shutdown = false;

    private static final int MOVEMENT_UPDATE_INTERVAL = 2;
    private static final int AI_UPDATE_INTERVAL = 600;

    protected int panicTicks = 0;
    protected boolean isOptimizedIdle = false;
    private int swimTicks = 0;

    private int movementUpdateCounter = 0;

    private volatile long lastAiUpdate = 0;

    public EntityWalkingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        initializeAiSystem();
        aiQueue.add(this);
    }

    private static synchronized void initializeAiSystem() {
        if (!initialized && !shutdown) {
            initialized = true;

            AI_SCHEDULER.scheduleAtFixedRate(() -> {
                try {
                    processAiQueue();
                } catch (Exception e) {
                }
            }, 0, AI_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private static void processAiQueue() {
        if (shutdown) return;

        long currentTime = System.currentTimeMillis();

        pendingAiTasks.entrySet().removeIf(entry -> {
            if (entry.getValue().isDone() || entry.getValue().isCancelled()) {
                return true;
            }
            return false;
        });

        for (EntityWalkingAnimal animal : aiQueue) {
            if (animal.closed || !animal.isAlive()) {
                aiQueue.remove(animal);
                pendingAiTasks.remove(animal);
                continue;
            }

            if (currentTime - animal.lastAiUpdate >= AI_UPDATE_INTERVAL) {
                Future<?> existingTask = pendingAiTasks.get(animal);
                if (existingTask == null || existingTask.isDone()) {
                    Future<?> future = AI_EXECUTOR.submit(() -> {
                        try {
                            animal.checkTarget();
                            animal.lastAiUpdate = System.currentTimeMillis();
                        } catch (Exception e) {
                        }
                    });

                    pendingAiTasks.put(animal, future);
                }
            }
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed || !this.isAlive()) {
            return super.onUpdate(currentTick);
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;

        if (this.panicTicks > 0) {
            this.panicTicks -= tickDiff;
            if (this.panicTicks <= 0) {
                this.moveMultiplier = 1.0f;
            }
        }

        this.entityBaseTick(tickDiff);

        movementUpdateCounter += tickDiff;
        if (movementUpdateCounter >= MOVEMENT_UPDATE_INTERVAL) {
            this.updateMove(movementUpdateCounter);
            movementUpdateCounter = 0;
        } else {
            this.applyWaterPhysics();
            this.move(this.motionX, this.motionY, this.motionZ);
        }

        return true;
    }

    protected void checkTarget() {
        if (this.panicTicks > 0) return;

        Player foodPlayer = null;
        double minDistSq = 225;

        for (Player p : this.level.getPlayers().values()) {
            double distSq = this.distanceSquared(p);
            if (distSq < minDistSq && this.isFeedItem(p.getInventory().getItemInHandFast())) {
                foodPlayer = p;
                minDistSq = distSq;
                break;
            }
        }

        if (foodPlayer != null) {
            this.target = foodPlayer;
            this.followTarget = foodPlayer;
            this.isOptimizedIdle = false;
        } else {
            if (!this.isOptimizedIdle) {
                int count = 0;
                for (Entity e : this.level.getNearbyEntities(this.boundingBox.grow(50, 10, 50))) {
                    if (e instanceof EntityWalkingAnimal) {
                        count++;
                        if (count >= 22) break;
                    }
                }

                if (count >= 22) {
                    this.isOptimizedIdle = Utils.rand(1, 100) > 5;
                } else {
                    this.isOptimizedIdle = false;
                }
            }

            if (!this.isOptimizedIdle && (this.target == null || this.distanceSquared(this.target) < 2)) {
                double rx = Utils.rand(5, 12) * (Utils.rand() ? 1 : -1);
                double rz = Utils.rand(5, 12) * (Utils.rand() ? 1 : -1);
                this.target = new Vector3(this.x + rx, this.y, this.z + rz);
                this.stayTime = 0;
            }
        }
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        if ((this.isOptimizedIdle && this.panicTicks <= 0) || isImmobile()) {
            this.motionX = 0;
            this.motionZ = 0;
            this.applyWaterPhysics();
            this.move(0, this.motionY, 0);
            return null;
        }

        if (this.target == null) return null;

        double dx = this.target.x - this.x;
        double dz = this.target.z - this.z;
        double dist = Math.abs(dx) + Math.abs(dz);

        this.applyWaterPhysics();

        if (this.stayTime > 0 && this.panicTicks <= 0) {
            this.stayTime -= tickDiff;
            this.motionX = 0;
            this.motionZ = 0;

            if (this.followTarget != null) {
                this.lookAt(this.followTarget);
            }
        } else if (dist > 0) {
            double speed = this.getSpeed() * moveMultiplier * (this.isInsideOfWater() ? 0.08 : 0.15);
            this.motionX = speed * (dx / dist);
            this.motionZ = speed * (dz / dist);

            this.setRotation(FastMath.toDegrees(-FastMath.atan2(dx, dz)), 0);
        }

        if (this.onGround && !this.isInsideOfWater()) {
            this.checkJump(this.motionX, this.motionZ);
        }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.updateMovement();

        return this.target;
    }

    private void applyWaterPhysics() {
        if (this.isInsideOfWater()) {
            this.swimTicks++;

            if (this.swimTicks >= 15) {
                this.motionY = 0.422;
                this.swimTicks = 0;
            } else {
                this.motionY = -0.028;
            }
        } else {
            this.swimTicks = 0;
            if (!this.onGround) {
                this.motionY -= this.getGravity();
            }
        }
    }

    protected void checkJump(double dx, double dz) {
        if (dx == 0 && dz == 0) return;

        double stepX = Math.signum(dx) * 0.5;
        double stepZ = Math.signum(dz) * 0.5;
        Vector3 ahead = new Vector3(
                NukkitMath.floorDouble(this.x + stepX),
                this.y,
                NukkitMath.floorDouble(this.z + stepZ)
        );

        Block block = this.level.getBlock(ahead);

        if (block instanceof BlockFenceGate) {
            BlockFenceGate gate = (BlockFenceGate) block;
            if (gate.isOpen()) {
                return;
            }
        }

        if (block instanceof BlockDoor) {
            BlockDoor door = (BlockDoor) block;
            if (door.isOpen()) {
                return;
            }
        }

        if (block.isSolid() || block instanceof BlockStairs || block.isLiquid()) {
            double blockTop = block.getBoundingBox() != null
                    ? block.getBoundingBox().getMaxY()
                    : ahead.y + 1;
            double diff = blockTop - this.y;

            if (diff <= 1.0 && !this.level.getBlock(ahead.up()).isSolid()) {
                this.motionY = 0.422;
            } else {
                this.stayTime = 10;
            }
        }
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);

        if (!ev.isCancelled() && ev instanceof EntityDamageByEntityEvent eev) {
            this.panicTicks = 120;
            this.moveMultiplier = 2.0f;

            Entity damager = eev.getDamager();
            if (damager != null) {
                double escapeX = this.x + (this.x - damager.x) * 10;
                double escapeZ = this.z + (this.z - damager.z) * 10;
                this.target = new Vector3(escapeX, this.y, escapeZ);
            }
        }

        return true;
    }

    public boolean isFeedItem(Item item) {
        return false;
    }

    @Override
    public void close() {
        aiQueue.remove(this);

        Future<?> task = pendingAiTasks.remove(this);
        if (task != null && !task.isDone()) {
            task.cancel(false);
        }

        super.close();
    }

    public static void shutdown() {
        shutdown = true;

        AI_SCHEDULER.shutdown();
        try {
            if (!AI_SCHEDULER.awaitTermination(2, TimeUnit.SECONDS)) {
                AI_SCHEDULER.shutdownNow();
            }
        } catch (InterruptedException e) {
            AI_SCHEDULER.shutdownNow();
            Thread.currentThread().interrupt();
        }

        AI_EXECUTOR.shutdown();
        try {
            if (!AI_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                AI_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            AI_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }

        aiQueue.clear();
        pendingAiTasks.clear();
        initialized = false;
    }

    public static int getQueueSize() {
        return aiQueue.size();
    }

    public static int getActiveTasksCount() {
        return pendingAiTasks.size();
    }
}