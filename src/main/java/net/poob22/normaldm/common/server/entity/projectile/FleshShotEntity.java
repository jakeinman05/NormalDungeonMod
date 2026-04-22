package net.poob22.normaldm.common.server.entity.projectile;

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
import org.jetbrains.annotations.NotNull;

public class FleshShotEntity extends Projectile {
    public FleshShotEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        hitEntity.hurt(hitEntity.damageSources().mobProjectile(this, (LivingEntity)this.getOwner()), 4.0F);
        this.discard();
    }

    private void doHitParticles() {
        ServerLevel level = (ServerLevel) this.level();
        level.sendParticles(NDMParticles.HURT_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), 4, 0.0F, 0.0F, 0.0F, 0.0F);
    }
}
