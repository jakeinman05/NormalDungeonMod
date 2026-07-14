package net.poob22.normaldm.common.server.items.stat_modifiers;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class EmptyHands extends StatItem
{
    public EmptyHands(Properties pProperties) {
        super(pProperties);
        setTitle("Empty Hands");
        setSubtitle("i feel nothing...");
    }

    @Override
    public void applyStats(Player player) {
        player.getCapability(COMBAT).ifPresent(c -> {
            c.getStats().setStat(StatType.ATTACK_SPEED, c.getStats().get(StatType.ATTACK_SPEED) * 1.25f);
        });
    }
}
