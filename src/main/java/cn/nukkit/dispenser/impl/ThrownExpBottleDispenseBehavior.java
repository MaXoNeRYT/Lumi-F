package cn.nukkit.dispenser.impl;

public class ThrownExpBottleDispenseBehavior extends ProjectileDispenseBehavior {

    public ThrownExpBottleDispenseBehavior() {
        super("ThrownExpBottle");
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
