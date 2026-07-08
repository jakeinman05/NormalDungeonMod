package net.poob22.normaldm.common.server.combat;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;
import net.poob22.normaldm.common.server.misc.NDMDamageTypes;

import java.util.Optional;
import java.util.function.Predicate;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class CombatUtil {
    private static final double SMALLEST_HITBOX_WIDTH = 1.2;

    public static EntityHitResult getEntityHitResult(Level pLevel, Entity pProjectile, Vec3 pStartVec, Vec3 pEndVec, AABB pBoundingBox, Predicate<Entity> pFilter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;

        for(Entity entity1 : pLevel.getEntities(pProjectile, pBoundingBox, pFilter)) {
            double entityWidth = entity1.getBbWidth();
            double inflationAmount = Math.max(0, (SMALLEST_HITBOX_WIDTH - entityWidth) / 2.0);

            AABB aabb = entity1.getBoundingBox().inflate(inflationAmount);

            Optional<Vec3> optional = aabb.clip(pStartVec, pEndVec);
            if (optional.isPresent()) {
                double d1 = pStartVec.distanceToSqr(optional.get());
                if (d1 < d0) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity);
    }

    public static boolean checkEntityHitResult(EntityHitResult entityHitResult, Player player) {
        if(entityHitResult != null) {
            Entity entity = entityHitResult.getEntity();
            var damageHolder = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(NDMDamageTypes.BEAM_DAMAGE);
            DamageSource source = new DamageSource(damageHolder, player);

            float damage = player.getCapability(COMBAT).map(c -> c.getStats().get(StatType.DAMAGE)).orElse(1.0f);
            return entity.hurt(source, damage);
        }
        return false;
    }

    public static double calculateReach(Player player, Vec3 deltaMovement) {
        Vec3 d = new Vec3(deltaMovement.x, deltaMovement.y, deltaMovement.z);
        double reach = player.getCapability(COMBAT).map(c -> c.getStats().get(StatType.REACH)).orElse(3.0f);
        double movementSpeed = player.onGround() ? (d.length() - 0.0784000015258789) : d.length();
        double forwardMovement = Math.max(0, d.dot(player.getLookAngle())) * 4;
        return reach + forwardMovement + movementSpeed;
    }
}
