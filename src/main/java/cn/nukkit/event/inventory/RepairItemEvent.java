package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RepairItemEvent extends InventoryEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private Item oldItem;
    private Item newItem;
    private Item materialItem;
    @Setter
    private int cost;
    private Player player;

    public RepairItemEvent(AnvilInventory inventory, Item oldItem, Item newItem, Item materialItem, int cost, Player player) {
        super(inventory);
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.materialItem = materialItem;
        this.cost = cost;
        this.player = player;
    }

}
