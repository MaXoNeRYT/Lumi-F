package cn.nukkit.dispenser.impl;

public class ArrowDispenseBehavior extends ProjectileDispenseBehavior {

    public ArrowDispenseBehavior() {
        super("Arrow");
    }

    @Override
    protected double getMotion() {
        return super.getMotion() * 1.5;
    }
}
