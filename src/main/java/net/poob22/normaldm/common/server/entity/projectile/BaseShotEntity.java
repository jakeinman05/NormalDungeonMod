package net.poob22.normaldm.common.server.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import org.jetbrains.annotations.NotNull;

public class BaseShotEntity extends Projectile {
    ParticleOptions hitParticle;

    public BaseShotEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHitParticles(NDMParticles.HURT_PARTICLE.get());
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = this.getDeltaMovement();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitResult))
            this.onHit(hitResult);
        double x = this.getX() + motion.x;
        double y = this.getY() + motion.y;
        double z = this.getZ() + motion.z;
        this.updateRotation();

        this.setPos(x, y, z);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        this.doHitParticles();
        this.discard();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity hitEntity = result.getEntity();
        if(!(hitEntity instanceof DungeonMob)) {
            hitEntity.hurt(hitEntity.damageSources().mobProjectile(this, (LivingEntity)this.getOwner()), 1.0F);
            this.doHitParticles();
            this.discard();
        }
    }

    private void doHitParticles() {
        if(!(this.level() instanceof ServerLevel)) return;

        ServerLevel level = (ServerLevel) this.level();
        level.sendParticles(this.getHitParticle(), this.getX(), this.getY(), this.getZ(), 4, 0.0F, 0.0F, 0.0F, 0.0F);
    }

    public void setHitParticles(ParticleOptions particle) {
        this.hitParticle = particle;
    }

    private ParticleOptions getHitParticle() {
        return this.hitParticle;
    }
}
