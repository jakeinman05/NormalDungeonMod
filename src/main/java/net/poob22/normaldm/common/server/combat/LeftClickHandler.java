package net.poob22.normaldm.common.server.combat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LeftClickHandler {
    // get player details from left click event
    // get values from player capability
    // get AttackDefinition from capability

    // if(cooldown > 0) no attack return
    // else call MainCombatHandler -> send in AttackDefinition
        // combat handler will manage attack based on definition

    public static void catchInput(Player player, Vec3 deltaMovement) {
        Level level = player.level();
        if(!level.isClientSide()) MainCombatHandler.punch(player, deltaMovement);
    }
}
