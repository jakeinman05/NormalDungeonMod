package net.poob22.normaldm.common.server.entity.projectile;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import org.jetbrains.annotations.NotNull;

public class SnotShotEntity extends BaseShotEntity {
    public SnotShotEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHitParticles(NDMParticles.SNOT_PARTICLE.get());
    }

    @Override
    public void tick() {
        if(this.level().isClientSide) {
            if(this.tickCount % (this.random.nextInt(5) + 5) == 0) {
                this.level().addParticle(NDMParticles.SNOT_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), -(this.getDeltaMovement().x * 0.3), 0.0D, -(this.getDeltaMovement().z * 0.3));
            }
        }

        super.tick();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity entity = result.getEntity();
        if(entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false, false));
        }
        super.onHitEntity(result);
    }
}
