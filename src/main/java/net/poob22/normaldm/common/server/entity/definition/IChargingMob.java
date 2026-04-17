package net.poob22.normaldm.common.server.entity.definition;

public interface IChargingMob {
    void setCharging(boolean charging);
    boolean isCharging();

    void wallHitReaction(double damage);
    void entityHitReaction();
}
