package cn.nukkit.dispenser.impl;

public class ThrownTridentDispenseBehavior extends ProjectileDispenseBehavior {

    public ThrownTridentDispenseBehavior() {
        super("ThrownTrident");
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
