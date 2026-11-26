package net.poob22.normaldm.common.server.items.stat_modifiers;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class LaysBag extends StatItem {
    public LaysBag(Properties pProperties) {
        super(pProperties);
        setTitle("Lays Bag");
        setSubtitle("mmmm.. yummy");
    }

    @Override
    public void applyStats(Player player) {
        player.getCapability(COMBAT).ifPresent(c -> {
            c.getStats().addToStat(StatType.HEALTH, 2f);
            c.getStats().applyPlayerAttributeStats(player);
            player.heal(2.0f);
        });
    }
}
