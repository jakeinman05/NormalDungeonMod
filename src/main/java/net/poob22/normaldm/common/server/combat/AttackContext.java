package net.poob22.normaldm.common.server.combat;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.capability.data.StatsDataComponent;

/// Add support for attack modifying items
public record AttackContext(Player player, StatsDataComponent stats) {}
