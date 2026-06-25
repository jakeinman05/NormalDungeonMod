package net.poob22.normaldm.common.server.combat;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.client.packet.ComboDataPacket;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

import static net.poob22.normaldm.common.client.packet.PacketHandler.sendToTracking;
import static net.poob22.normaldm.common.server.combat.CombatUtil.*;
import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT_DATA_CAPABILITY;

public class MainCombatHandler {
    public static void punch(Player player, Vec3 deltaMovement) {
        double reach = calculateReach(player, deltaMovement);

        Vec3 from = player.getEyePosition();
        Vec3 to = from.add(player.getViewVector(1.0F).scale(reach));

        AABB searchBox = player.getBoundingBox().expandTowards(player.getViewVector(1.0F).scale(reach)).inflate(1.0D);

        EntityHitResult entityHitResult = getEntityHitResult(player.level(), player, from, to, searchBox, entity -> entity instanceof DungeonMob);

        boolean flag = checkEntityHitResult(entityHitResult, player);

        // if flag, check combos and increment them
        if (flag) {
            player.getCapability(COMBAT_DATA_CAPABILITY).ifPresent(cap -> {
                cap.getCombosData().incrementCombos();
                // remove
                sendToTracking(player, new ComboDataPacket(cap.getCombosData().getCombos()));
            });
        }

        ServerLevel world = (ServerLevel)player.level();
        world.sendParticles(ParticleTypes.END_ROD, to.x, to.y, to.z, 1, 0, 0, 0, 0);
    }

}
