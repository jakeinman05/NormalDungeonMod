package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.poob22.normaldm.NormalDungeonMod;
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

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        for(StatType type : StatType.values()) {
            tag.putFloat(type.getSerializedName(), stats.get(type));
            NormalDungeonMod.LOGGER.info("Saved stat " + type.getSerializedName() + " with value: " + stats.get(type));
        }

        return tag;
    }

    public void load(CompoundTag tag) {
        for(StatType type : StatType.values()) {
            setStat(type, tag.getFloat(type.getSerializedName()));
            NormalDungeonMod.LOGGER.info("Loaded stat " + type.getSerializedName() + " with value: " + stats.get(type));
        }
    }

    public float getStat(StatType type) {
        return stats.get(type);
    }
}
