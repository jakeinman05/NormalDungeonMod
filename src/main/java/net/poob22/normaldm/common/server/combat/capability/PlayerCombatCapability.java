package net.poob22.normaldm.common.server.combat.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.packet.combat.ComboDataPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.packet.combat.ComponentDataPacket;
import net.poob22.normaldm.common.client.packet.combat.CooldownDataPacket;
import net.poob22.normaldm.common.client.packet.combat.StatsDataPacket;
import net.poob22.normaldm.common.server.combat.capability.data.CombosDataComponent;
import net.poob22.normaldm.common.server.combat.capability.data.CooldownDataComponent;
import net.poob22.normaldm.common.server.combat.capability.data.StatsDataComponent;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

public class PlayerCombatCapability {
    private boolean SHOW_DEBUG = false;

    private final CombosDataComponent combos = new CombosDataComponent();
    private final CooldownDataComponent cooldown = new CooldownDataComponent();
    private final StatsDataComponent stats = new StatsDataComponent();

    public CombosDataComponent getCombosData() {
        return combos;
    }

    public CooldownDataComponent getCooldownData() {
        return cooldown;
    }

    public StatsDataComponent getStats() {
        return stats;
    }

    public boolean showDebug() {
        return SHOW_DEBUG;
    }

    public void toggleDebug(boolean b) {
        SHOW_DEBUG = b;
    }

    public void toggleDebug() {
        this.SHOW_DEBUG = !this.SHOW_DEBUG;
    }

    public void tick(Player player) {
        combos.tick(player);
        cooldown.tick(player);
        stats.tick(player);

        // networking
        if(SHOW_DEBUG) {
            if(combos.isDirty()) {
                NormalDungeonMod.LOGGER.info("Sent combo packet");
                sendPacket(player, new ComboDataPacket(combos.getCombos(), combos.getComboTimer()));
                combos.clearDirty();
            }
            if(cooldown.isDirty()) {
                NormalDungeonMod.LOGGER.info("Sent cooldown packet");
                sendPacket(player, new CooldownDataPacket(cooldown.getCooldown()));
                cooldown.clearDirty();
            }
            if(stats.isDirty()) {
                NormalDungeonMod.LOGGER.info("Sent stats packet");
                sendPacket(player, new StatsDataPacket(stats.get(StatType.DAMAGE), stats.get(StatType.REACH), stats.get(StatType.HEALTH), stats.get(StatType.ATTACK_SPEED), stats.get(StatType.MOVEMENT_SPEED), stats.get(StatType.COMBO_MULTIPLIER)));
                stats.clearDirty();
            }
        }
    }

    private void sendPacket(Player player, ComponentDataPacket packet) {
        PacketHandler.sendToTracking(player, packet);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.put("Stats", stats.save());

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        stats.load(tag.getCompound("Stats"));
    }
}
