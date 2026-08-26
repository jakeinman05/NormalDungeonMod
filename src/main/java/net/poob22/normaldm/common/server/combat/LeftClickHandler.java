package net.poob22.normaldm.common.server.combat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class LeftClickHandler {
    /// where attack execution happens
    public static void catchInput(Player player, Vec3 deltaMovement) {
        Level level = player.level();
        if(!level.isClientSide() && checkPunch(player)) {
            player.getCapability(COMBAT).ifPresent(c -> {
                AttackContext context = new AttackContext(player, c.getStats());
                MainCombatHandler.punch(player, deltaMovement, context);
            });
//            MainCombatHandler.punch(player, deltaMovement);
        }
    }

    private static boolean checkPunch(Player player) {
        var capability = player.getCapability(COMBAT);
        boolean flag = (capability.isPresent() && capability.map(c -> c.getCooldownData().noCooldown()).orElse(false));

        if(flag) capability.ifPresent(c -> {
            // grab cooldown from capability
            c.getCooldownData().setCooldown(20/c.getStats().get(StatType.ATTACK_SPEED));
        });

        return flag;
    }
}
