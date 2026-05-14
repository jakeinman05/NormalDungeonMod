package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.poob22.normaldm.common.server.entity.ai.DungeonMobMeleeGoal;
import net.poob22.normaldm.common.server.entity.ai.RandomlyAnimatedAttackGoal;

import java.util.EnumSet;
import java.util.List;

public class BigFellaEntity extends AnimatedRandomlyAttackingMob {

    public BigFellaEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setHurtParticleAmount(10);
        setDeathParticleAmount(100);
        setAttackOnTick(18);
        setDefaultAttackTicks(30);
        resetAttackTicks();
        setDefaultAttackInterval(120);
        resetAttackInterval();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.23D).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BigFellaRandomAttackGoal(this));
        this.goalSelector.addGoal(1, new DungeonMobMeleeGoal(this, 1.0D));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void aiStep() {
        this.yBodyRot = Mth.rotLerp(0.08F, this.yBodyRot, this.getYRot());

        super.aiStep();

        if(isAttacking()) {
            this.getNavigation().stop();
        }
    }

    @Override
    public void performAttack() {
        AABB bb = this.getBoundingBox();
        AABB box = new AABB(bb.minX - 3, bb.minY, bb.minZ - 3, bb.maxX + 3, bb.maxY - 1.6875, bb.maxZ + 3);

        List<LivingEntity> entitiesAround = this.level().getEntitiesOfClass(LivingEntity.class, box);

        for(LivingEntity entity : entitiesAround) {
            if(entity instanceof DungeonMob) continue;

            boolean flag = entity.hurt(entity.damageSources().mobAttack(this), (float)this.getAttributeBaseValue(Attributes.ATTACK_DAMAGE));

            if(flag) {
                double d0 = entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                double d1 = Math.max(0.0D, 1.0D - d0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, (double)0.4F * d1, 0.0D));
                this.doEnchantDamageEffects(this, entity);
            }
        }

        if(level() instanceof ServerLevel level) {
            for(int i = -3; i < 3; ++i) {
                for(int j = -3; j < 3; ++j) {
                    BlockPos pos = new BlockPos((int)this.getX() + i, (int)this.getY(), (int)this.getZ() + j);
                    BlockState state = level.getBlockState(pos.below());
                    BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
                    level.sendParticles(particle, pos.getX(), pos.getY(), pos.getZ(), 5, random.nextDouble(), 0, random.nextDouble(), random.nextDouble());
                }
            }
        }
    }

    static class BigFellaRandomAttackGoal extends RandomlyAnimatedAttackGoal {
        public BigFellaRandomAttackGoal(AnimatedRandomlyAttackingMob mob) {
            super(mob);
            setFlags(EnumSet.of(Flag.MOVE));
        }
    }
}
