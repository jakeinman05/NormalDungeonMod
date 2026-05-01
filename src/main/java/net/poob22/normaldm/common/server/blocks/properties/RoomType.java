package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RoomType implements StringRepresentable {
    SMALL,
    SMALL_HALLWAY,
    MEDIUM,
    LARGE;


    @Override
    public @NotNull String getSerializedName() {
        return switch(this) {
            case SMALL -> "small";
            case SMALL_HALLWAY -> "small_hallway";
            case MEDIUM -> "medium";
            case LARGE -> "large";
        };
    }
}
