package net.poob22.normaldm.common.server.combat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT_DATA_CAPABILITY;

public class LeftClickHandler {
    // get player details from left click event
    // get values from player capability
    // get AttackDefinition from capability

    // if(cooldown > 0) no attack return
    // else call MainCombatHandler -> send in AttackDefinition
        // combat handler will manage attack based on definition

    public static void catchInput(Player player, Vec3 deltaMovement) {
        Level level = player.level();
        if(!level.isClientSide() && checkPunch(player)) MainCombatHandler.punch(player, deltaMovement);
    }

    private static boolean checkPunch(Player player) {
        var capability = player.getCapability(COMBAT_DATA_CAPABILITY);
        boolean flag = (capability.isPresent() && capability.map(c -> c.getCooldownData().noCooldown()).orElse(false));

        if(flag) capability.ifPresent(c -> {
            c.getCooldownData().setCooldown(10);
        });

        return flag;
    }
}
