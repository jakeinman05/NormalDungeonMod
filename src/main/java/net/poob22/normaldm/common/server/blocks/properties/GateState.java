package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum GateState implements StringRepresentable {
    CLOSED, // blank eye, static closed state | will act as 'unlocked'
    OPEN, // invisible, no-collision
    OPENING, // animated state
    LOCKED; // red eye, when in an active room

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
        return switch (this) {
            case CLOSED -> "CLOSED";
            case OPEN -> "OPEN";
            case OPENING -> "OPENING";
            case LOCKED -> "LOCKED";
            default -> "UNKNOWN";
        };
    }
}
