package net.poob22.normaldm.common.server.items.stat_modifiers;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class WoodenSpoon extends StatItem {
    public WoodenSpoon(Properties pProperties) {
        super(pProperties);
        setTitle("Wooden Spoon");
        setSubtitle("");
    }

    @Override
    public void applyStats(Player player) {
        player.getCapability(COMBAT).ifPresent(c -> {
            c.getStats().addToStat(StatType.REACH, 0.5f);
            c.getStats().addToStat(StatType.MOVEMENT_SPEED, 0.02f);
            c.getStats().applyPlayerAttributeStats(player);
        });

    }
}
