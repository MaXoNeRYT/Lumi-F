package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.attribute.EntityMovementSpeedModifier;

import java.awt.*;

public class EffectSlowness extends Effect {

    public EffectSlowness() {
        super(EffectType.SLOWNESS, "%potion.moveSlowdown", new Color(139, 175, 224), true);
    }

    @Override
    public void onAdd(Entity entity) {
        if (entity instanceof EntityLiving living) {
            living.addMovementSpeedModifier(new EntityMovementSpeedModifier("minecraft:slowness_effect", 1 - 0.15f * this.getLevel(), EntityMovementSpeedModifier.Operation.MULTIPLY));
        }
    }

    @Override
    public void onRemove(Entity entity) {
        if (entity instanceof EntityLiving living) {
            living.removeMovementSpeedModifier("minecraft:slowness_effect");
        }
    }
}
