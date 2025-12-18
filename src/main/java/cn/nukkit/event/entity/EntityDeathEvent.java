package cn.nukkit.event.entity;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EntityDeathEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item[] drops;
    private int experience;

    public EntityDeathEvent(EntityLiving entity, int experience) {
        this(entity, Item.EMPTY_ARRAY, experience);
    }

    public EntityDeathEvent(EntityLiving entity, Item[] drops, int experience) {
        this.experience = experience;
        this.entity = entity;
        this.drops = drops;
    }

    public Item[] getDrops() {
        return drops;
    }

    public void setDrops(Item[] drops) {
        if (drops == null) {
            drops = Item.EMPTY_ARRAY;
        }
        this.drops = drops;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
