package net.poob22.normaldm.common.server.combat;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.capability.data.StatsDataComponent;

public record AttackContext(Player player, StatsDataComponent stats) {

}
