package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import java.util.EnumMap;

public class StatsDataComponent extends SyncableComponent {
    private final EnumMap<StatType, Float> baseStats = new EnumMap<>(StatType.class);
    private final EnumMap<StatType, Float> stats = new EnumMap<>(StatType.class);

    public StatsDataComponent() {
        baseStats.put(StatType.DAMAGE, 1.0f);
        baseStats.put(StatType.REACH, 3.0f);
        baseStats.put(StatType.HEALTH, 6.0f);
        baseStats.put(StatType.ATTACK_SPEED, 5.0f); // ticks
        baseStats.put(StatType.MOVEMENT_SPEED, 0.1f);
        baseStats.put(StatType.COMBO_MULTIPLIER, 1.2f);
        stats.clear();
        stats.putAll(baseStats);
    }

    public void resetStats(Player player) {
        stats.clear();
        stats.putAll(baseStats);
        applyPlayerAttributeStats(player);
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

    public void applyPlayerAttributeStats(Player player) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.get(StatType.MOVEMENT_SPEED));
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.get(StatType.HEALTH));

        if(player.getHealth() > this.get(StatType.HEALTH)) {
            player.setHealth(this.get(StatType.HEALTH));
        }
    }

    public void sync(float damage, float reach, float health, float atkSpeed, float moveSpeed, float cooldownMult) {
        stats.put(StatType.DAMAGE, damage);
        stats.put(StatType.REACH, reach);
        stats.put(StatType.HEALTH, health);
        stats.put(StatType.ATTACK_SPEED, atkSpeed);
        stats.put(StatType.MOVEMENT_SPEED, moveSpeed);
        stats.put(StatType.COMBO_MULTIPLIER, cooldownMult);
    }

    public StatsDataComponent copy() {
        StatsDataComponent copy = new StatsDataComponent();
        copy.setStat(StatType.DAMAGE, this.get(StatType.DAMAGE));
        copy.setStat(StatType.REACH, this.get(StatType.REACH));
        copy.setStat(StatType.HEALTH, this.get(StatType.HEALTH));
        copy.setStat(StatType.ATTACK_SPEED, this.get(StatType.ATTACK_SPEED));
        copy.setStat(StatType.MOVEMENT_SPEED, this.get(StatType.MOVEMENT_SPEED));
        copy.setStat(StatType.COMBO_MULTIPLIER, this.get(StatType.COMBO_MULTIPLIER));
        return copy;
    }

    public void tick(Player player) {}

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

    public float get(StatType type) {
        return stats.get(type);
    }
}
