package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.packet.combat.ComboDataPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.packet.combat.ComponentDataPacket;
import net.poob22.normaldm.common.client.packet.combat.CooldownDataPacket;
import net.poob22.normaldm.common.client.packet.combat.StatsDataPacket;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

public class PlayerCombatData {
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

    public void tick(Player player) {
        combos.tick(player);
        cooldown.tick(player);

        // networking
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
            sendPacket(player, new StatsDataPacket(stats.getStat(StatType.DAMAGE), stats.getStat(StatType.REACH), stats.getStat(StatType.HEALTH), stats.getStat(StatType.ATTACK_SPEED), stats.getStat(StatType.MOVEMENT_SPEED), stats.getStat(StatType.COMBO_MULTIPLIER)));
            stats.clearDirty();
        }
    }

    private <T> void sendPacket(Player player, ComponentDataPacket packet) {
        PacketHandler.sendToTracking(player, packet);
    }
}
