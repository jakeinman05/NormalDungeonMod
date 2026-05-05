package net.poob22.normaldm.common.server.entity.definition;

public interface IReloadingMob {
    boolean isReloaded();
    void setReloaded(boolean reloaded);
    int getReloadTime(int reloadTime); // define a RELOAD_TIME in mob
}
