package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.attribute.EntityMovementSpeedModifier;

import java.awt.*;

public class EffectSpeed extends Effect {

    public EffectSpeed() {
        super(EffectType.SPEED, "%potion.moveSpeed", new Color(51, 235, 255));
    }

    @Override
    public void onAdd(Entity entity) {
        if (entity instanceof EntityLiving living) {
            living.addMovementSpeedModifier(new EntityMovementSpeedModifier("minecraft:speed_effect", 1 + 0.2f * this.getLevel(), EntityMovementSpeedModifier.Operation.MULTIPLY));
        }
    }

    @Override
    public void onRemove(Entity entity) {
        if (entity instanceof EntityLiving living) {
            living.removeMovementSpeedModifier("minecraft:speed_effect");
        }
    }
}
