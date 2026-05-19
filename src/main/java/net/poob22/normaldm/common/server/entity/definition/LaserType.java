package net.poob22.normaldm.common.server.entity.definition;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LaserType implements StringRepresentable {
    STRAIGHT,
    HOMING,
    FOLLOWING;

    @Override
    public @NotNull String getSerializedName() {
        return switch(this) {
            case STRAIGHT -> "straight";
            case HOMING -> "homing";
            case FOLLOWING -> "following";
        };
    }
}
