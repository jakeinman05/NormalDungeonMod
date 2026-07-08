package net.poob22.normaldm.common.server.combat.capability.data;

public abstract class SyncableComponent {
    private boolean dirty;

    public boolean isDirty() {
        return this.dirty;
    }

    void markDirty() {
        this.dirty = true;
    }

    public void clearDirty() {
        this.dirty = false;
    }
}
