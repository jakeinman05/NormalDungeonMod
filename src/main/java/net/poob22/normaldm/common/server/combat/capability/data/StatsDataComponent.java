package net.poob22.normaldm.common.server.combat.capability.data;

import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import java.util.EnumMap;

public class StatsDataComponent extends SyncableComponent {
    private final EnumMap<StatType, Float> baseStats = new EnumMap<>(StatType.class);
    private final EnumMap<StatType, Float> stats = new EnumMap<>(StatType.class);

    public StatsDataComponent() {
        baseStats.put(StatType.DAMAGE, 1.0f);
        baseStats.put(StatType.REACH, 3.0f);
        baseStats.put(StatType.HEALTH, 20.0f);
        baseStats.put(StatType.ATTACK_SPEED, 5.0f); // ticks
        baseStats.put(StatType.MOVEMENT_SPEED, 1.0f); // multiplier of base movement speed
        baseStats.put(StatType.COMBO_MULTIPLIER, 1.2f);
        stats.clear();
        stats.putAll(baseStats);
    }

    public void resetStats() {
        stats.clear();
        stats.putAll(baseStats);
        markDirty();
    }

    public void setStat(StatType type, float value) {
        stats.put(type, value);
        markDirty();
    }

    public void modifyStat(StatType type, float value) {
        stats.put(type, stats.get(type) + value);
        markDirty();
    }

    public void sync(float damage, float reach, float health, float atkSpeed, float moveSpeed, float cooldownMult) {
        stats.put(StatType.DAMAGE, damage);
        stats.put(StatType.REACH, reach);
        stats.put(StatType.HEALTH, health);
        stats.put(StatType.ATTACK_SPEED, atkSpeed);
        stats.put(StatType.MOVEMENT_SPEED, moveSpeed);
        stats.put(StatType.COMBO_MULTIPLIER, cooldownMult);
    }

    public float getStat(StatType type) {
        return stats.get(type);
    }
}
