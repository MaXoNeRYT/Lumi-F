package cn.nukkit.dispenser.impl;

public class ThrownPotionDispenseBehavior extends ProjectileDispenseBehavior {

    public ThrownPotionDispenseBehavior() {
        super("ThrownPotion");
    }

    @Override
    protected float getAccuracy() {
        return super.getAccuracy() * 0.5f;
    }

    @Override
    protected double getMotion() {
        return super.getMotion() * 1.25;
    }
}
