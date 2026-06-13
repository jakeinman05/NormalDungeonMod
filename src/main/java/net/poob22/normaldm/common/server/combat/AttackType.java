package net.poob22.normaldm.common.server.combat;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum AttackType implements StringRepresentable {
    LIGHT_ATTACK,
    HEAVY_ATTACK;

    public String toString() {
        return getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
        return switch(this) {
            case LIGHT_ATTACK -> "light_attack";
            case HEAVY_ATTACK -> "heavy_attack";
        };
    }
}
