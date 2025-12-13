package cn.nukkit.network.process.processor.common;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemCrossbow;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author SocialMoods
 */
@Slf4j
public class InventoryTransactionProcessor extends DataPacketProcessor<InventoryTransactionPacket> {

    public static final InventoryTransactionProcessor INSTANCE = new InventoryTransactionProcessor();

    private static final int SMITHING_WINDOW_ID = 60;
    private static final int MAX_FAILED_TRANSACTIONS = 15;
    private static final long CLICK_SPAM_THRESHOLD = 100;

    @Override
    public void handle(@NotNull PlayerHandle handle, @NotNull InventoryTransactionPacket packet) {
        if (!handle.isSpawned() || !handle.isAlive()) {
            log.debug("Player {} sent inventory transaction packet while not spawned or not alive", handle.getUsername());
            return;
        }

        if (handle.player.isSpectator()) {
            handle.setNeedSendInventory(true);
            return;
        }

        // Nasty hack because the client won't change the right packet in survival when creating netherite stuff
        packet = fixSmithingTransactionPacket(handle, packet);

        List<InventoryAction> actions = createInventoryActions(handle, packet);
        if (actions == null || actions.isEmpty()) {
            return;
        }

        if (actions.size() > 50) {
            handle.close("", "Client sent invalid packet");
            return;
        }
        
        if (handleSpecialTransactions(handle, packet, actions)) {
            return;
        }
        
        if (handlePendingTransactions(handle, packet, actions)) {
            return;
        }
        
        switch (packet.transactionType) {
            case InventoryTransactionPacket.TYPE_NORMAL:
                handleNormalTransaction(handle, packet, actions);
                break;
            case InventoryTransactionPacket.TYPE_MISMATCH:
                handleMismatchTransaction(handle, packet);
                break;
            case InventoryTransactionPacket.TYPE_USE_ITEM:
                handleUseItemTransaction(handle, packet);
                break;
            case InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY:
                handleUseItemOnEntityTransaction(handle, packet);
                break;
            case InventoryTransactionPacket.TYPE_RELEASE_ITEM:
                handleReleaseItemTransaction(handle, packet);
                break;
            default:
                handle.player.getInventory().sendContents(handle.player);
                break;
        }
    }

    private InventoryTransactionPacket fixSmithingTransactionPacket(PlayerHandle handle, InventoryTransactionPacket packet) {
        if (packet.transactionType == InventoryTransactionPacket.TYPE_MISMATCH) {
            Inventory inventory = handle.player.getWindowById(SMITHING_WINDOW_ID);
            if (inventory instanceof SmithingInventory smithingInventory && !smithingInventory.getResult().isNull()) {
                InventoryTransactionPacket fixedPacket = createFixedSmithingPacket(handle, smithingInventory);
                if (fixedPacket != null) {
                    return fixedPacket;
                }
            }
        }
        return packet;
    }

    private InventoryTransactionPacket createFixedSmithingPacket(PlayerHandle handle, SmithingInventory smithingInventory) {
        InventoryTransactionPacket fixedPacket = new InventoryTransactionPacket();
        fixedPacket.isRepairItemPart = true;
        fixedPacket.actions = new NetworkInventoryAction[8];

        Item fromIngredient = smithingInventory.getIngredient().clone();
        Item toIngredient = fromIngredient.decrement(1);
        Item fromEquipment = smithingInventory.getEquipment().clone();
        Item toEquipment = fromEquipment.decrement(1);
        Item fromTemplate = smithingInventory.getTemplate().clone();
        Item toTemplate = fromTemplate.decrement(1);
        Item fromResult = Item.get(Item.AIR);
        Item toResult = smithingInventory.getResult().clone();
        
        fixedPacket.actions[0] = createInventoryAction(ContainerIds.UI, SmithingInventory.SMITHING_INGREDIENT_UI_SLOT, fromIngredient, toIngredient);
        fixedPacket.actions[1] = createInventoryAction(ContainerIds.UI, SmithingInventory.SMITHING_EQUIPMENT_UI_SLOT, fromEquipment, toEquipment);
        fixedPacket.actions[2] = createInventoryAction(ContainerIds.UI, SmithingInventory.SMITHING_TEMPLATE_UI_SLOT, fromTemplate, toTemplate);

        int emptyPlayerSlot = findEmptyPlayerSlot(handle);
        if (emptyPlayerSlot == -1) {
            handle.setNeedSendInventory(true);
            return null;
        }

        fixedPacket.actions[3] = createInventoryAction(ContainerIds.INVENTORY, emptyPlayerSlot, Item.get(Item.AIR), toResult);
        fixedPacket.actions[4] = createInventoryAction(NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT, 2, toResult, fromResult);
        fixedPacket.actions[5] = createInventoryAction(NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT, 0, toEquipment, fromEquipment);
        fixedPacket.actions[6] = createInventoryAction(NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL, 1, toIngredient, fromIngredient);
        fixedPacket.actions[7] = createInventoryAction(NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL, 3, toTemplate, fromTemplate);

        return fixedPacket;
    }

    private NetworkInventoryAction createInventoryAction(int windowId, int slot, Item oldItem, Item newItem) {
        NetworkInventoryAction action = new NetworkInventoryAction();
        action.windowId = windowId;
        action.inventorySlot = slot;
        action.oldItem = oldItem.clone();
        action.newItem = newItem.clone();
        return action;
    }

    private int findEmptyPlayerSlot(PlayerHandle handle) {
        PlayerInventory inventory = handle.player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot).isNull()) {
                return slot;
            }
        }
        return -1;
    }

    private List<InventoryAction> createInventoryActions(PlayerHandle handle, InventoryTransactionPacket packet) {
        List<InventoryAction> actions = new ArrayList<>();
        for (NetworkInventoryAction networkAction : packet.actions) {
            InventoryAction action = networkAction.createInventoryAction(handle.player);
            if (action == null) {
                log.debug("Unmatched inventory action from {}: {}", handle.getUsername(), networkAction);
                handle.setNeedSendInventory(true);
                return null;
            }
            actions.add(action);
        }
        return actions;
    }

    private boolean handleSpecialTransactions(PlayerHandle handle, InventoryTransactionPacket packet, List<InventoryAction> actions) {
        if (packet.isCraftingPart) {
            return handleCraftingTransaction(handle, packet, actions);
        } else if (packet.isEnchantingPart) {
            return handleEnchantTransaction(handle, actions);
        } else if (packet.isRepairItemPart) {
            return handleRepairItemTransaction(handle, actions);
        } else if (packet.isTradeItemPart) {
            return handleTradeTransaction(handle, actions);
        }
        return false;
    }

    private boolean handleCraftingTransaction(PlayerHandle handle, InventoryTransactionPacket packet, List<InventoryAction> actions) {
        if (LoomTransaction.isIn(actions)) {
            handleLoomTransaction(handle, actions);
            return true;
        }

        CraftingTransaction transaction = handle.getCraftingTransaction();
        if (transaction == null) {
            transaction = new CraftingTransaction(handle.player, actions);
            handle.setCraftingTransaction(transaction);
        } else {
            for (InventoryAction action : actions) {
                transaction.addAction(action);
            }
        }

        if (transaction.getPrimaryOutput() != null && transaction.canExecute()) {
            try {
                transaction.execute();
            } catch (Exception e) {
                log.warn("Executing crafting transaction failed", e);
            }
            handle.setCraftingTransaction(null);
        }
        return true;
    }

    private void handleLoomTransaction(PlayerHandle handle, List<InventoryAction> actions) {
        LoomTransaction transaction = handle.getLoomTransaction();
        if (transaction == null) {
            transaction = new LoomTransaction(handle.player, actions);
            handle.setLoomTransaction(transaction);
        } else {
            for (InventoryAction action : actions) {
                transaction.addAction(action);
            }
        }
        
        if (transaction.canExecute()) {
            if (transaction.execute()) {
                handle.player.getLevel().addLevelSoundEvent(handle.player, LevelSoundEventPacket.SOUND_BLOCK_LOOM_USE);
            }
        }
        handle.setLoomTransaction(null);
    }

    private boolean handleEnchantTransaction(PlayerHandle handle, List<InventoryAction> actions) {
        EnchantTransaction transaction = handle.getEnchantTransaction();
        if (transaction == null) {
            transaction = new EnchantTransaction(handle.player, actions);
            handle.setEnchantTransaction(transaction);
        } else {
            for (InventoryAction action : actions) {
                transaction.addAction(action);
            }
        }
        
        if (transaction.canExecute()) {
            transaction.execute();
            handle.setEnchantTransaction(null);
        }
        return true;
    }

    private boolean handleRepairItemTransaction(PlayerHandle handle, List<InventoryAction> actions) {
        Sound sound = null;
        
        if (SmithingTransaction.isIn(actions)) {
            SmithingTransaction transaction = handle.getSmithingTransaction();
            if (transaction == null) {
                transaction = new SmithingTransaction(handle.player, actions);
                handle.setSmithingTransaction(transaction);
            } else {
                for (InventoryAction action : actions) {
                    transaction.addAction(action);
                }
            }
            
            if (transaction.canExecute()) {
                try {
                    if (transaction.execute()) {
                        sound = Sound.SMITHING_TABLE_USE;
                    }
                } finally {
                    handle.setSmithingTransaction(null);
                }
            }
        } else if (GrindstoneTransaction.isIn(actions)) {
            GrindstoneTransaction transaction = handle.getGrindstoneTransaction();
            if (transaction == null) {
                transaction = new GrindstoneTransaction(handle.player, actions);
                handle.setGrindstoneTransaction(transaction);
            } else {
                for (InventoryAction action : actions) {
                    transaction.addAction(action);
                }
            }
            
            if (transaction.canExecute()) {
                if (transaction.execute()) {
                    Level level = handle.player.getLevel();
                    Collection<Player> players = level.getChunkPlayers(
                        handle.player.getChunkX(), handle.player.getChunkZ()).values();
                    players.remove(handle.player);
                    if (!players.isEmpty()) {
                        level.addLevelSoundEvent(handle.player, 
                            LevelSoundEventPacket.SOUND_BLOCK_GRINDSTONE_USE);
                    }
                }
                handle.setGrindstoneTransaction(null);
            }
        } else {
            RepairItemTransaction transaction = handle.getRepairItemTransaction();
            if (transaction == null) {
                transaction = new RepairItemTransaction(handle.player, actions);
                handle.setRepairItemTransaction(transaction);
            } else {
                for (InventoryAction action : actions) {
                    transaction.addAction(action);
                }
            }
            
            if (transaction.canExecute()) {
                transaction.execute();
                handle.setRepairItemTransaction(null);
            }
        }

        if (sound != null) {
            Level level = handle.player.getLevel();
            Collection<Player> players = level.getChunkPlayers(
                    handle.player.getChunkX(), handle.player.getChunkZ()).values();
            players.remove(handle.player);
            if (!players.isEmpty()) {
                level.addSound(handle.player, sound, 1f, 1f, players);
            }
        }
        return true;
    }

    private boolean handleTradeTransaction(PlayerHandle handle, List<InventoryAction> actions) {
        TradingTransaction transaction = handle.getTradingTransaction();
        if (transaction == null) {
            transaction = new TradingTransaction(handle.player, actions);
            handle.setTradingTransaction(transaction);
        } else {
            for (InventoryAction action : actions) {
                transaction.addAction(action);
            }
        }
        
        if (transaction.canExecute()) {
            transaction.execute();
            
            for (Inventory inventory : transaction.getInventories()) {
                if (inventory instanceof TradeInventory tradeInventory) {
                    EntityVillager ent = tradeInventory.getHolder();
                    ent.namedTag.putBoolean("traded", true);
                    
                    for (Tag tag : ent.getRecipes().getAll()) {
                        CompoundTag ta = (CompoundTag) tag;
                        if (ta.getCompound("buyA").getShort("id") == tradeInventory.getItem(0).getId()) {
                            int tradeXP = ta.getInt("traderExp");
                            handle.player.addExperience(ta.getByte("rewardExp"));
                            ent.addExperience(tradeXP);
                            handle.player.getLevel().addSound(handle.player, Sound.RANDOM_ORB, 0, 3f, handle.player);
                        }
                    }
                }
            }
            
            handle.setTradingTransaction(null);
        }
        return true;
    }

    private boolean handlePendingTransactions(PlayerHandle handle, InventoryTransactionPacket packet, List<InventoryAction> actions) {
        // Check pending crafting transaction
        CraftingTransaction craftingTransaction = handle.getCraftingTransaction();
        if (craftingTransaction != null) {
            if (craftingTransaction.checkForCraftingPart(actions)) {
                for (InventoryAction action : actions) {
                    craftingTransaction.addAction(action);
                }
                return true;
            } else {
                handleInvalidTransaction(handle, "crafting");
                handle.setCraftingTransaction(null);
                return false;
            }
        }

        EnchantTransaction enchantTransaction = handle.getEnchantTransaction();
        if (enchantTransaction != null) {
            if (enchantTransaction.checkForEnchantPart(actions)) {
                for (InventoryAction action : actions) {
                    enchantTransaction.addAction(action);
                }
                return true;
            } else {
                handleInvalidTransaction(handle, "enchant");
                handle.setEnchantTransaction(null);
                return false;
            }
        }

        RepairItemTransaction repairTransaction = handle.getRepairItemTransaction();
        if (repairTransaction != null) {
            if (RepairItemTransaction.checkForRepairItemPart(actions)) {
                for (InventoryAction action : actions) {
                    repairTransaction.addAction(action);
                }
                return true;
            } else {
                handleInvalidTransaction(handle, "repair item");
                handle.setRepairItemTransaction(null);
                return false;
            }
        }

        SmithingTransaction smithingTransaction = handle.getSmithingTransaction();
        if (smithingTransaction != null) {
            if (SmithingTransaction.isIn(actions)) {
                for (InventoryAction action : actions) {
                    smithingTransaction.addAction(action);
                }
                return true;
            } else {
                log.debug("Got unexpected normal inventory action with incomplete smithing table transaction from {}, refusing to execute use the smithing table {}", 
                    handle.player.getName(), packet);
                handle.player.removeAllWindows(false);
                handle.setNeedSendInventory(true);
                handle.setSmithingTransaction(null);
                return false;
            }
        }

        GrindstoneTransaction grindstoneTransaction = handle.getGrindstoneTransaction();
        if (grindstoneTransaction != null) {
            if (GrindstoneTransaction.isIn(actions)) {
                for (InventoryAction action : actions) {
                    grindstoneTransaction.addAction(action);
                }
                return true;
            } else {
                handleInvalidTransaction(handle, "grindstone");
                handle.setGrindstoneTransaction(null);
                return false;
            }
        }

        return false;
    }

    private void handleInvalidTransaction(PlayerHandle handle, String transactionType) {
        log.debug("Got unexpected normal inventory action with incomplete {} transaction from {}, refusing to execute", 
            transactionType, handle.getUsername());
        handle.player.removeAllWindows(false);
        handle.setNeedSendInventory(true);
    }

    private void handleNormalTransaction(PlayerHandle handle, InventoryTransactionPacket packet, List<InventoryAction> actions) {
        InventoryTransaction transaction = new InventoryTransaction(handle.player, actions);
        
        if (!transaction.execute()) {
            log.debug("Failed to execute inventory transaction from {} with actions: {}", 
                handle.getUsername(), Arrays.toString(packet.actions));
            handle.incrementFailedTransactions();
            
            if (handle.getFailedTransactions() > MAX_FAILED_TRANSACTIONS) {
                handle.close("", "Too many failed inventory transactions");
            }
        }
    }

    private void handleMismatchTransaction(PlayerHandle handle, InventoryTransactionPacket packet) {
        if (packet.actions.length > 0) {
            log.debug("Expected 0 actions for mismatch, got {}, {}", 
                packet.actions.length, Arrays.toString(packet.actions));
        }
        handle.setNeedSendInventory(true);
    }

    private void handleUseItemTransaction(PlayerHandle handle, InventoryTransactionPacket packet) {
        UseItemData useItemData;
        BlockVector3 blockVector;

        useItemData = (UseItemData) packet.transactionData;
        blockVector = useItemData.blockPos;
        BlockFace face = useItemData.face;

        PlayerInventory inventory = handle.player.getInventory();
        if (inventory.getHeldItemIndex() != useItemData.hotbarSlot) {
            inventory.equipItem(useItemData.hotbarSlot);
        }

        switch (useItemData.actionType) {
            case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                handleClickBlock(handle, useItemData, blockVector, face);
                break;
            case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                handleBreakBlock(handle, useItemData, blockVector, face);
                break;
            case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
                handleClickAir(handle, useItemData, face);
                break;
            default:
                break;
        }
    }

    private void handleClickBlock(PlayerHandle handle, UseItemData useItemData, BlockVector3 blockVector, BlockFace face) {
        boolean spamming = !Server.getInstance().getSettings().player().doNotLimitInteractions()
                && handle.getLastRightClickPos() != null
                && System.currentTimeMillis() - handle.getLastRightClickTime() < CLICK_SPAM_THRESHOLD
                && blockVector.distanceSquared(handle.getLastRightClickPos()) < 0.00001;

        handle.setLastRightClickPos(blockVector);
        handle.setLastRightClickTime(System.currentTimeMillis());

        // Hack: Fix client spamming right clicks
        if (spamming && handle.player.getInventory().getItemInHandFast().getBlockId() == BlockID.AIR) {
            return;
        }

        handle.player.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);

        if (handle.player.canInteract(blockVector.asVector3().add(0.5, 0.5, 0.5), handle.isCreative() ? 13 : 7)) {
            Item handItem = handle.player.getInventory().getItemInHand();
            
            if (handle.isCreative()) {
                if (handle.player.getLevel().useItemOn(blockVector.asVector3(), handItem, face, 
                    useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, handle.player) != null) {
                    return;
                }
            } else if (handItem.equals(useItemData.itemInHand)) {
                Item item = handItem;
                Item oldItem = item.clone();
                
                if ((item = handle.player.getLevel().useItemOn(blockVector.asVector3(), item, face,
                    useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, handle.player)) != null) {
                    
                    if (!item.equals(oldItem) || item.getCount() != oldItem.getCount()) {
                        updatePlayerHandItem(handle, oldItem, item);
                    }
                    return;
                }
            } else {
                handle.setNeedSendHeldItem(true);
            }
        }

        if (blockVector.distanceSquared(handle.player) > 10000) {
            return;
        }

        sendBlockUpdates(handle, blockVector, face);
    }

    private void handleBreakBlock(PlayerHandle handle, UseItemData useItemData, BlockVector3 blockVector, BlockFace face) {
        if (!handle.isSpawned() || !handle.isAlive()) {
            return;
        }

        handle.resetCraftingGridType();

        Item handItem = handle.player.getInventory().getItemInHand();
        Item oldItem = handItem.clone();

        if (handle.player.canInteract(blockVector.asVector3().add(0.5, 0.5, 0.5), handle.isCreative() ? 13 : 7)) {
            Item resultItem = handle.player.getLevel().useBreakOn(blockVector.asVector3(), face, handItem, handle.player, true);
            
            if (resultItem != null) {
                if (!handle.isCreative()) {
                    handle.player.getFoodData().exhaust(0.005);
                    if (!resultItem.equals(oldItem) || resultItem.getCount() != oldItem.getCount()) {
                        updatePlayerHandItem(handle, oldItem, resultItem);
                    }
                }
                return;
            }
        }

        handle.player.getInventory().sendContents(handle.player);
        handle.player.getInventory().sendHeldItem(handle.player);

        if (blockVector.distanceSquared(handle.player) < 10000) {
            sendBlockReset(handle, blockVector);
        }
    }

    private void handleClickAir(PlayerHandle handle, UseItemData useItemData, BlockFace face) {
        Vector3 directionVector = handle.player.getDirectionVector();
        PlayerInventory inventory = handle.player.getInventory();

        if (inventory.getHeldItemIndex() != useItemData.hotbarSlot) {
            inventory.equipItem(useItemData.hotbarSlot);
        }

        Item handItem = inventory.getItemInHand();

        // Handle crossbow separately
        if (handItem instanceof ItemCrossbow) {
            if (!handItem.onClickAir(handle.player, directionVector)) {
                return; // Shoot
            }
        }

        if (!handItem.equalsFast(useItemData.itemInHand)) {
            handle.setNeedSendHeldItem(true);
            return;
        }

        PlayerInteractEvent interactEvent = new PlayerInteractEvent(handle.player, handItem, 
            directionVector, face, PlayerInteractEvent.Action.RIGHT_CLICK_AIR);
        handle.callEvent(interactEvent);

        if (interactEvent.isCancelled()) {
            handle.setNeedSendHeldItem(true);
            return;
        }

        if (handItem.onClickAir(handle.player, directionVector)) {
            if (!handle.isCreative()) {
                updatePlayerHandItem(handle, inventory.getItemInHandFast(), handItem);
            }

            if (!handle.isUsingItem()) {
                handle.setUsingItem(handItem.canRelease());
                return;
            }

            int ticksUsed = Server.getInstance().getTick() - handle.getStartAction();
            handle.setUsingItem(false);
            if (!handItem.onUse(handle.player, ticksUsed)) {
                inventory.sendContents(handle.player);
            }
        }
    }

    private void handleUseItemOnEntityTransaction(PlayerHandle handle, InventoryTransactionPacket packet) {
        UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) packet.transactionData;
        Entity target = handle.player.getLevel().getEntity(useItemOnEntityData.entityRuntimeId);
        
        if (target == null) {
            return;
        }

        PlayerInventory inventory = handle.player.getInventory();
        if (inventory.getHeldItemIndex() != useItemOnEntityData.hotbarSlot) {
            inventory.equipItem(useItemOnEntityData.hotbarSlot);
        }

        Item handItem = inventory.getItemInHand();
        if (!useItemOnEntityData.itemInHand.equalsFast(handItem)) {
            inventory.sendHeldItem(handle.player);
        }

        switch (useItemOnEntityData.actionType) {
            case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT:
                handleEntityInteract(handle, target, handItem, useItemOnEntityData);
                break;
            case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK:
                handleEntityAttack(handle, target, handItem);
                break;
            default:
                break;
        }
    }

    private void handleEntityInteract(PlayerHandle handle, Entity target, Item item, UseItemOnEntityData data) {
        if (handle.player.distanceSquared(target) > 256) {
            log.debug("{}: target entity is too far away", handle.getUsername());
            return;
        }

        handle.setBreakingBlock(null);
        handle.setUsingItem(false);

        PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(handle.player, target, item, data.clickPos);
        if (handle.player.isSpectator()) event.setCancelled();
        handle.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (target.onInteract(handle.player, item, data.clickPos) && !handle.isCreative()) {
            if (item.isTool()) {
                if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                    handle.player.getLevel().addSoundToViewers(handle.player, Sound.RANDOM_BREAK);
                    handle.player.getLevel().addParticle(new ItemBreakParticle(handle.player, item));
                    item = new ItemBlock(Block.get(BlockID.AIR));
                }
            } else {
                if (item.count > 1) {
                    item.count--;
                } else {
                    item = new ItemBlock(Block.get(BlockID.AIR));
                }
            }
            updatePlayerHandItem(handle, handle.player.getInventory().getItemInHandFast(), item);
        }
    }

    private void handleEntityAttack(PlayerHandle handle, Entity target, Item item) {
        if (target.getId() == handle.getId()) {
            handle.kick(PlayerKickEvent.Reason.INVALID_PVP, "Tried to attack invalid player");
            return;
        }

        if (!handle.player.canInteractEntity(target, handle.isCreative() ? 8 : 5)) {
            return;
        } else if (target instanceof Player) {
            if ((((Player) target).gamemode & 0x01) > 0) {
                return;
            } else if (!Server.getInstance().getSettings().world().allowPvp()) {
                return;
            }
        }

        handle.setBreakingBlock(null);
        handle.setUsingItem(false);

        if (handle.player.getSleepingPos() != null) {
            log.debug("{}: USE_ITEM_ON_ENTITY_ACTION_ATTACK while sleeping", handle.getUsername());
            return;
        }

        if (handle.isInventoryOpen()) {
            log.debug("{}: USE_ITEM_ON_ENTITY_ACTION_ATTACK while viewing inventory", handle.getUsername());
            return;
        }

        Enchantment[] enchantments = item.getEnchantments();
        float itemDamage = item.getAttackDamage(handle.player);
        for (Enchantment enchantment : enchantments) {
            itemDamage += (float) enchantment.getDamageBonus(target, handle.player);
        }

        Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
        damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);

        float knockBack = 0.3f;
        Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
        if (knockBackEnchantment != null) {
            knockBack += knockBackEnchantment.getLevel() * 0.1f;
        }

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(handle.player, target,
            EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, knockBack, enchantments);
        event.setBreakShield(item.canBreakShield());
        
        if (handle.player.isSpectator()) event.setCancelled();
        if ((target instanceof Player) && !handle.player.getLevel().getGameRules().getBoolean(GameRule.PVP)) {
            event.setCancelled();
        }

        if (!item.onAttack(handle.player, target)) {
            handle.player.getInventory().sendContents(handle.player);
        }

        if (!target.attack(event)) {
            if (item.isTool() && !handle.isCreative()) {
                handle.player.getInventory().sendContents(handle.player);
            }
            return;
        }

        for (Enchantment enchantment : item.getEnchantments()) {
            enchantment.doPostAttack(handle.player, target);
        }

        if (item.isTool() && !handle.isCreative()) {
            if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                handle.player.getLevel().addSoundToViewers(handle.player, Sound.RANDOM_BREAK);
                handle.player.getLevel().addParticle(new ItemBreakParticle(handle.player, item));
                handle.player.getInventory().setItemInHand(Item.get(0));
            } else {
                updatePlayerHandItem(handle, handle.player.getInventory().getItemInHandFast(), item);
            }
        }
    }

    private void handleReleaseItemTransaction(PlayerHandle handle, InventoryTransactionPacket packet) {
        if (handle.player.isSpectator()) {
            handle.setNeedSendInventory(true);
            return;
        }

        ReleaseItemData releaseItemData = (ReleaseItemData) packet.transactionData;

        try {
            if (releaseItemData.actionType == InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE) {
                if (handle.isUsingItem()) {
                    Item handItem = handle.player.getInventory().getItemInHand();
                    int ticksUsed = Server.getInstance().getTick() - handle.getStartAction();
                    if (!handItem.onRelease(handle.player, ticksUsed)) {
                        handle.player.getInventory().sendContents(handle.player);
                    }
                    handle.setUsingItem(false);
                } else {
                    handle.player.getInventory().sendContents(handle.player);
                }
            } else {
                log.debug("{}: unknown release item action type: {}",
                        handle.getUsername(), releaseItemData.actionType);
            }
        } finally {
            handle.setUsingItem(false);
        }
    }

    private void updatePlayerHandItem(PlayerHandle handle, Item oldItem, Item newItem) {
        if (oldItem.getId() == newItem.getId() || newItem.getId() == 0) {
            handle.player.getInventory().setItemInHand(newItem);
        } else {
            log.debug("Tried to set item {} but {} had item {} in their hand slot", 
                newItem.getId(), handle.getUsername(), oldItem.getId());
        }
        handle.player.getInventory().sendHeldItem(handle.player.getViewers().values());
    }

    private void sendBlockUpdates(PlayerHandle handle, BlockVector3 blockVector, BlockFace face) {
        Level level = handle.player.getLevel();
        Block target = level.getBlock(blockVector.asVector3());
        Block block = target.getSide(face);

        level.sendBlocks(new Player[]{handle.player}, new Block[]{target, block}, UpdateBlockPacket.FLAG_NOGRAPHIC);
        level.sendBlocks(new Player[]{handle.player}, 
            new Block[]{target.getLevelBlockAtLayer(1), block.getLevelBlockAtLayer(1)}, 
            UpdateBlockPacket.FLAG_NOGRAPHIC, 1);

        if (target instanceof BlockDoor door) {
            if ((door.getDamage() & 0x08) > 0) {
                Block part = target.down();
                if (part.getId() == target.getId()) {
                    target = part;
                    level.sendBlocks(new Player[]{handle.player}, new Block[]{target}, UpdateBlockPacket.FLAG_NOGRAPHIC);
                    level.sendBlocks(new Player[]{handle.player}, 
                        new Block[]{target.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_NOGRAPHIC, 1);
                }
            }
        }
    }

    private void sendBlockReset(PlayerHandle handle, BlockVector3 blockVector) {
        Level level = handle.player.getLevel();
        Block target = level.getBlock(blockVector.asVector3());
        level.sendBlocks(new Player[]{handle.player}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY);

        BlockEntity blockEntity = level.getBlockEntity(blockVector.asVector3());
        if (blockEntity instanceof BlockEntitySpawnable) {
            ((BlockEntitySpawnable) blockEntity).spawnTo(handle.player);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.INVENTORY_TRANSACTION_PACKET;
    }

    @Override
    public Class<? extends InventoryTransactionPacket> getPacketClass() {
        return InventoryTransactionPacket.class;
    }
}