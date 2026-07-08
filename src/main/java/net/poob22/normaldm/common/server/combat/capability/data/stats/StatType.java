package net.poob22.normaldm.common.server.combat.capability.data.stats;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum StatType implements StringRepresentable {
    DAMAGE,
    REACH,
    HEALTH,
    ATTACK_SPEED,
    MOVEMENT_SPEED,
    COMBO_MULTIPLIER;

    @Override
    public @NotNull String getSerializedName() {
        return switch(this) {
            case DAMAGE -> "damage";
            case REACH -> "reach";
            case HEALTH -> "health";
            case ATTACK_SPEED -> "attack_speed";
            case MOVEMENT_SPEED -> "movement_speed";
            case COMBO_MULTIPLIER -> "combo_multiplier";
        };
    }
}
