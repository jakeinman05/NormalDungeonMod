package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RoomType implements StringRepresentable {
    SQUARE,
    HALLWAY,
    L_SHAPED;


    @Override
    public @NotNull String getSerializedName() {
        return switch(this) {
            case SQUARE -> "square";
            case HALLWAY -> "hallway";
            case L_SHAPED -> "l_shaped";
        };
    }
}
