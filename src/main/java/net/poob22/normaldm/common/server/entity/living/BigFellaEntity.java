package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.ai.DungeonMobMeleeGoal;
import net.poob22.normaldm.common.server.entity.ai.RandomlyAttackGoal;

import java.util.EnumSet;
import java.util.List;

public class BigFellaEntity extends AbstractRandomlyAttackingMob {

    public BigFellaEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setHurtParticleAmount(10);
        setDeathParticleAmount(100);
        setDefaultAttackTicks(30);
        setAttackOnTick(18);
        setDefaultAttackInterval(120);
        resetAttackInterval();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.23D).add(Attributes.ATTACK_DAMAGE, 1).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
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
        if(isAttacking()) {
            this.getNavigation().stop();
        }

        super.aiStep();
    }

    @Override
    public void performAttack() {
        List<LivingEntity> entitiesAround = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2));

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
    }

    static class BigFellaRandomAttackGoal extends RandomlyAttackGoal {
        public BigFellaRandomAttackGoal(AbstractRandomlyAttackingMob mob) {
            super(mob);
            setFlags(EnumSet.of(Flag.MOVE));
        }
    }
}
