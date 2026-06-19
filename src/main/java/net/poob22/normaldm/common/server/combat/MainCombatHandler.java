package net.poob22.normaldm.common.server.combat;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import net.poob22.normaldm.common.server.misc.NDMDamageTypes;

import java.util.Optional;
import java.util.function.Predicate;

public class MainCombatHandler {
    private static final double SMALLEST_HITBOX_WIDTH = 1.2;

    public static void punch(Player player, Vec3 deltaMovement) {
        double reach = calculateReach(player, deltaMovement);

        Vec3 from = player.getEyePosition();
        Vec3 to = from.add(player.getViewVector(1.0F).scale(reach));

        AABB searchBox = player.getBoundingBox().expandTowards(player.getViewVector(1.0F).scale(reach)).inflate(1.0D);

        EntityHitResult entityHitResult = getEntityHitResult(player.level(), player, from, to, searchBox, entity -> entity instanceof DungeonMob);

        if(entityHitResult != null) {
            Entity entity = entityHitResult.getEntity();
            var damageHolder = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(NDMDamageTypes.BEAM_DAMAGE);
            DamageSource source = new DamageSource(damageHolder, player);
            entity.hurt(source, 1.0F);
        }

        ServerLevel world = (ServerLevel)player.level();
        world.sendParticles(ParticleTypes.END_ROD, to.x, to.y, to.z, 1, 0, 0, 0, 0);
    }

    public static double calculateReach(Player player, Vec3 deltaMovement) {
        Vec3 d = new Vec3(deltaMovement.x, deltaMovement.y, deltaMovement.z);
        double reach = 3.0D; // will grab this from capabilities later
        double movementSpeed = player.onGround() ? (d.length() - 0.0784000015258789) : d.length();
        double forwardMovement = Math.max(0, d.dot(player.getLookAngle())) * 4;
        return reach + forwardMovement + movementSpeed;
    }

    public static EntityHitResult getEntityHitResult(Level pLevel, Entity pProjectile, Vec3 pStartVec, Vec3 pEndVec, AABB pBoundingBox, Predicate<Entity> pFilter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;

        for(Entity entity1 : pLevel.getEntities(pProjectile, pBoundingBox, pFilter)) {
            double entityWidth = entity1.getBbWidth();

            double inflationAmount = Math.max(0, (SMALLEST_HITBOX_WIDTH - entityWidth) / 2.0);

            AABB aabb = entity1.getBoundingBox().inflate(inflationAmount);
            NormalDungeonMod.LOGGER.info("Inflation Amount: " + inflationAmount);
            NormalDungeonMod.LOGGER.info("AABB x: " + aabb.getXsize() + " AABB z: " + aabb.getZsize());
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
}
