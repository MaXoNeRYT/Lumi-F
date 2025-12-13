package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowDialog;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerUIInventory;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.PlayerFogPacket;
import cn.nukkit.network.protocol.types.ExperimentData;
import cn.nukkit.network.protocol.types.PlayerBlockActionData;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.bossbar.DummyBossBar;
import cn.nukkit.utils.LoginChainData;
import com.google.common.cache.Cache;
import com.google.common.collect.BiMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A PlayerHandle is used to access a player's protected data.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class PlayerHandle {
    public final @NotNull Player player;

    public PlayerHandle(@NotNull Player player) {
        this.player = player;
    }

    public int getProtocol() {
        return player.protocol;
    }

    public NetworkPlayerSession getNetworkSession() {
        return player.networkSession;
    }


    public void sendPlayStatus(int status) {
        player.sendPlayStatus(status);
    }

    public void sendPlayStatus(int status, boolean immediate) {
        player.sendPlayStatus(status, immediate);
    }

    public SourceInterface getInterfaz() {
        return player.interfaz;
    }

    public BiMap<Inventory, Integer> getWindows() {
        return player.windows;
    }

    public BiMap<Integer, Inventory> getWindowIndex() {
        return player.windowIndex;
    }

    public Set<Integer> getPermanentWindows() {
        return player.permanentWindows;
    }

    public LongLinkedOpenHashSet getLoadQueue() {
        return player.loadQueue;
    }

    public Map<UUID, Player> getHiddenPlayers() {
        return player.hiddenPlayers;
    }

    public int getWindowCnt() {
        return player.windowCnt;
    }

    public void setWindowCnt(int windowCnt) {
        player.windowCnt = windowCnt;
    }

    public void setClosingWindowId(int closingWindowId) {
        player.closingWindowId = closingWindowId;
    }

    public PlayerUIInventory getPlayerUIInventory() {
        return player.playerUIInventory;
    }

    public void setPlayerUIInventory(PlayerUIInventory playerUIInventory) {
        player.playerUIInventory = playerUIInventory;
    }

    public CraftingTransaction getCraftingTransaction() {
        return player.craftingTransaction;
    }

    public void setCraftingTransaction(CraftingTransaction craftingTransaction) {
        player.craftingTransaction = craftingTransaction;
    }

    public EnchantTransaction getEnchantTransaction() {
        return player.enchantTransaction;
    }

    public void setEnchantTransaction(EnchantTransaction enchantTransaction) {
        player.enchantTransaction = enchantTransaction;
    }

    public RepairItemTransaction getRepairItemTransaction() {
        return player.repairItemTransaction;
    }

    public void setRepairItemTransaction(RepairItemTransaction repairItemTransaction) {
        player.repairItemTransaction = repairItemTransaction;
    }

    public SmithingTransaction getSmithingTransaction() {
        return player.smithingTransaction;
    }

    public void setSmithingTransaction(SmithingTransaction smithingTransaction) {
        player.smithingTransaction = smithingTransaction;
    }

    public TradingTransaction getTradingTransaction() {
        return player.tradingTransaction;
    }

    public void setTradingTransaction(TradingTransaction tradingTransaction) {
        player.tradingTransaction = tradingTransaction;
    }

    public long getRandomClientId() {
        return player.randomClientId;
    }

    public void setRandomClientId(long randomClientId) {
        player.randomClientId = randomClientId;
    }

    public Vector3 getForceMovement() {
        return player.forceMovement;
    }

    public void setForceMovement(Vector3 forceMovement) {
        player.forceMovement = forceMovement;
    }

    public Vector3 getTeleportPosition() {
        return player.teleportPosition;
    }

    public void setTeleportPosition(Vector3 teleportPosition) {
        player.teleportPosition = teleportPosition;
    }

    public void setConnected(boolean connected) {
        player.connected = connected;
    }

    public boolean isRemoveFormat() {
        return player.removeFormat;
    }

    public String getUsername() {
        return player.username;
    }

    public void setUsername(String username) {
        player.username = username;
    }

    public String getIusername() {
        return player.iusername;
    }

    public void setIusername(String iusername) {
        player.iusername = iusername;
    }

    public String getDisplayName() {
        return player.displayName;
    }

    public void setDisplayName(String displayName) {
        player.displayName = displayName;
    }

    public int getStartAction() {
        return player.startAction;
    }

    public void setStartAction(int startAction) {
        player.startAction = startAction;
    }

    public Vector3 getSleeping() {
        return player.sleeping;
    }

    public void setSleeping(Vector3 sleeping) {
        player.sleeping = sleeping;
    }

    public int getNextChunkOrderRun() {
        return player.nextChunkOrderRun;
    }

    public void setNextChunkOrderRun(int nextChunkOrderRun) {
        player.nextChunkOrderRun = nextChunkOrderRun;
    }

    public Vector3 getNewPosition() {
        return player.newPosition;
    }

    public void setNewPosition(Vector3 newPosition) {
        player.newPosition = newPosition;
    }

    public int getChunkRadius() {
        return player.chunkRadius;
    }

    public void setChunkRadius(int chunkRadius) {
        player.chunkRadius = chunkRadius;
    }

    public Position getSpawnPosition() {
        return player.spawnPosition;
    }

    public void setSpawnPosition(Position spawnPosition) {
        player.spawnPosition = spawnPosition;
    }

    public void setInAirTicks(int inAirTicks) {
        player.inAirTicks = inAirTicks;
    }

    public int getStartAirTicks() {
        return player.startAirTicks;
    }

    public void setStartAirTicks(int startAirTicks) {
        player.startAirTicks = startAirTicks;
    }

    public boolean isCheckMovement() {
        return player.checkMovement;
    }

    public void setFoodData(PlayerFood foodData) {
        player.foodData = foodData;
    }

    public int getFormWindowCount() {
        return player.formWindowCount;
    }

    public void setFormWindowCount(int formWindowCount) {
        player.formWindowCount = formWindowCount;
    }

    public Map<Integer, FormWindow> getFormWindows() {
        return player.formWindows;
    }

    public void setFormWindows(Map<Integer, FormWindow> formWindows) {
        player.formWindows = formWindows;
    }

    public Map<Integer, FormWindow> getServerSettings() {
        return player.serverSettings;
    }

    public void setServerSettings(Map<Integer, FormWindow> serverSettings) {
        player.serverSettings = serverSettings;
    }

    public Cache<String, FormWindowDialog> getDialogWindows() {
        return player.dialogWindows;
    }

    public void setDialogWindows(Cache<String, FormWindowDialog> dialogWindows) {
        player.dialogWindows = dialogWindows;
    }

    public void setDummyBossBars(Map<Long, DummyBossBar> dummyBossBars) {
        player.dummyBossBars = dummyBossBars;
    }

    public boolean isShouldLogin() {
        return player.shouldLogin;
    }

    public void setShouldLogin(boolean shouldLogin) {
        player.shouldLogin = shouldLogin;
    }

    public List<PlayerFogPacket.Fog> getFogStack() {
        return player.fogStack;
    }

    public void setFogStack(List<PlayerFogPacket.Fog> fogStack) {
        player.fogStack = fogStack;
    }

    public void setLoginChainData(LoginChainData loginChainData) {
        player.loginChainData = loginChainData;
    }

    public LoginChainData getLoginChainData() {
        return player.loginChainData;
    }

    public boolean isVerified() {
        return player.loginVerified;
    }

    public void setVerified(boolean verified) {
        player.loginVerified = verified;
    }

    public boolean isAwaitingEncryptionHandshake() {
        return player.awaitingEncryptionHandshake;
    }

    public void setAwaitingEncryptionHandshake(boolean awaitingEncryptionHandshake) {
        player.awaitingEncryptionHandshake = awaitingEncryptionHandshake;
    }

    public AsyncTask getPreLoginEventTask() {
        return player.preLoginEventTask;
    }

    public void setPreLoginEventTask(AsyncTask preLoginEventTask) {
        player.preLoginEventTask = preLoginEventTask;
    }

    public void completeLoginSequence() {
        player.completeLoginSequence();
    }

    public void processLogin() {
        player.processLogin();
    }

    public void processPreLogin() {
        player.processPreLogin();
    }

    public void doFirstSpawn() {
        player.doFirstSpawn();
    }

    public boolean isLoginPacketReceived() {
        return player.loginPacketReceived;
    }

    public int getFailedMobEquipmentPacket() {
        return player.failedMobEquipmentPacket;
    }

    public void setFailedMobEquipmentPacket(int failedCount) {
        player.failedMobEquipmentPacket = failedCount;
    }

    public void setLoginPacketReceived(boolean value) {
        player.loginPacketReceived = value;
    }

    public void setProtocol(int protocol) {
        player.protocol = protocol;
    }

    public String getUnverifiedUsername() {
        return player.unverifiedUsername;
    }

    public void setUnverifiedUsername(String name) {
        player.unverifiedUsername = name;
    }

    public void setSocketAddress(InetSocketAddress address) {
        player.socketAddress = address;
    }

    public void setVersion(String version) {
        player.version = version;
    }

    public void setLoginUuid(UUID uuid) {
        player.setLoginUuid(uuid);
    }

    public void setUuid(UUID uuid) {
        player.setUuid(uuid);
    }

    public void setRawUUID(byte[] raw) {
        player.setRawUuid(raw);
    }

    public void close(String reason, String message) {
        player.close(reason, message);
    }

    public boolean isShouldPack() {
        return player.shouldPack;
    }

    public void setShouldPack(boolean value) {
        player.shouldPack = value;
    }

    public List<ExperimentData> getExperiments() {
        return player.getExperiments();
    }

    public boolean isSpawned() {
        return player.spawned;
    }

    public boolean isAlive() {
        return player.isAlive();
    }

    public void setRotation(double yaw, double pitch, double headYaw) {
        player.setRotation(yaw, pitch, headYaw);
    }

    public float getBaseOffset() {
        return player.getBaseOffset();
    }

    public void sendPosition(
            Vector3 pos,
            double yaw,
            double pitch,
            int mode
    ) {
        player.sendPosition(pos, yaw, pitch, mode);
    }


    public boolean isMovementServerAuthoritative() {
        return player.isMovementServerAuthoritative();
    }

    public boolean isLockMovementInput() {
        return player.isLockMovementInput();
    }

    public int getLastTeleportTick() {
        return player.lastTeleportTick;
    }

    public double getLastX() {
        return player.lastX;
    }

    public double getLastY() {
        return player.lastY;
    }

    public double getLastZ() {
        return player.lastZ;
    }

    public Vector3 getTemporalVector() {
        return player.temporalVector;
    }

    public Entity getRiding() {
        return player.riding;
    }

    public void setRotation(float yaw, float pitch, float headYaw) {
        player.setRotation(yaw, pitch, headYaw);
    }

    public void offerClientMovement(Vector3 pos) {
        player.clientMovements.offer(pos);
    }


    public void sendPosition(Vector3 pos, float yaw, float pitch, int mode) {
        player.sendPosition(pos, yaw, pitch, mode);
    }

    public void sendPosition(Player p, float yaw, float pitch, int mode) {
        player.sendPosition(p, yaw, pitch, mode);
    }


    public void setNeedSendData(boolean v) {
        player.setNeedSendData(v);
    }

    public void setNeedSendAdventureSettings(boolean v) {
        player.needSendAdventureSettings = v;
    }

    public PlayerBlockActionData getLastBlockAction() {
        return player.lastBlockAction;
    }

    public void setLastBlockAction(PlayerBlockActionData action) {
        player.lastBlockAction = action;
    }
    public void onBlockBreakComplete(BlockVector3 pos, BlockFace face) {
        player.onBlockBreakComplete(pos, face);
    }

    public void onSpinAttack(int level) {
        player.onSpinAttack(level);
    }

    public boolean isOnline() { return player.isOnline(); }
    public boolean isCreative() { return player.isCreative(); }
    public boolean isSwimming() { return player.isSwimming(); }
    public boolean isGliding() { return player.isGliding(); }
    public boolean isInsideOfWater() { return player.isInsideOfWater(); }
    public boolean isServerAuthoritativeBlockBreaking() { return player.isServerAuthoritativeBlockBreaking(); }

    public void respawn() { player.respawn(); }
    public void stopSleep() { player.stopSleep(); }
    public void kick(PlayerKickEvent.Reason reason, String message) { player.kick(reason, message); }
    public void kick(PlayerKickEvent.Reason reason, String message, boolean notify, String extra) {
        player.kick(reason, message, notify, extra);
    }

    public void onBlockBreakStart(Vector3 pos, BlockFace face) { player.onBlockBreakStart(pos, face); }
    public void onBlockBreakAbort(Vector3 pos, BlockFace face) { player.onBlockBreakAbort(pos, face); }
    public void onBlockBreakContinue(Vector3 pos, BlockFace face) { player.onBlockBreakContinue(pos, face); }

    public void setSprinting(boolean value) { player.setSprinting(value); }
    public void setSneaking(boolean value) { player.setSneaking(value); }
    public void setSwimming(boolean value) { player.setSwimming(value); }
    public void setGliding(boolean value) { player.setGliding(value); }
    public void setCrawling(boolean value) { player.setCrawling(value); }
    public void setSpinAttack(boolean value) { player.setSpinAttack(value); }
    public void setUsingItem(boolean value) { player.setUsingItem(value); }

    public Item getChestplate() { return player.getInventory().getChestplateFast(); }

    public void callEvent(Event event) {
        player.getServer().getPluginManager().callEvent(event);
    }

    public void addSound(Vector3 pos, Sound sound) {
        player.level.addSound(pos, sound);
    }

    public boolean checkFlightAllowed() {
        return player.getServer().getSettings().player().allowFlight() || player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT);
    }

    public double getYaw() { return player.yaw; }
    public double getPitch() { return player.pitch; }

    public boolean hasRidingOrSleeping() { return player.riding != null || player.sleeping != null; }

    public int getInAirTicks() { return player.inAirTicks; }

    public void setButtonText(String text) { player.setButtonText(text); }

    public boolean isInventoryOpen() { return player.inventoryOpen; }

    public void openInventory(InventoryHolder holder) { player.addWindow(holder.getInventory()); }

    public long getId() { return player.getId(); }

    public void setInventoryOpen(boolean open) { player.inventoryOpen = open; }

    public void setNoShieldTicks(int ticks) { player.setNoShieldTicks(ticks); }

    public int getLastEating() {
        return player.lastEating;
    }

    public void setLastEating(int ticks) { player.lastEating = ticks; }

    public void setCraftingType(int type) { player.craftingType = type; }

    public void resetCraftingGridType() {
        player.resetCraftingGridType();
    }

    public int getCraftingType() {
        return player.craftingType;
    }

    public void removeWindow(Inventory key, boolean b) {
        player.removeWindow(key, b);
    }

    public void setNeedSendInventory(boolean needSendInventory) {
        player.needSendInventory = needSendInventory;
    }

    public boolean isNeedSendInventory() {
        return player.needSendInventory;
    }

    public void setNeedSendHeldItem(boolean needSendHeldItem) {
        player.needSendHeldItem = needSendHeldItem;
    }

    public boolean isNeedSendHeldItem() {
        return player.needSendHeldItem;
    }

    public boolean isUsingItem() {
        return player.isUsingItem();
    }

    public void setLastRightClickPos(BlockVector3 pos) {
        player.lastRightClickPos = pos;
    }

    public BlockVector3 getLastRightClickPos() {
        return player.lastRightClickPos;
    }

    public void setLastRightClickTime(long time) {
        player.lastRightClickTime = time;
    }

    public double getLastRightClickTime() {
        return player.lastRightClickTime;
    }

    public void setBreakingBlock(Block breakingBlock) {
        player.breakingBlock = breakingBlock;
    }

    public Block getBreakingBlock() {
        return player.breakingBlock;
    }

    public void setGrindstoneTransaction(GrindstoneTransaction transaction) {
        player.grindstoneTransaction = transaction;
    }

    public GrindstoneTransaction getGrindstoneTransaction() {
        return player.grindstoneTransaction;
    }

    public void setLoomTransaction(LoomTransaction transaction) {
        player.loomTransaction = transaction;
    }

    public LoomTransaction getLoomTransaction() {
        return player.loomTransaction;
    }

    public void incrementFailedTransactions() {
        player.failedTransactions++;
    }

    public int getFailedTransactions() {
        return player.failedTransactions;
    }
}