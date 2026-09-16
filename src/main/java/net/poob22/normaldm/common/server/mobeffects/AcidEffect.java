package net.poob22.normaldm.common.server.mobeffects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.entity.ai.AiUtil;

public class AcidEffect extends MobEffect {
    int damageInterval = 10;
    int ticksTillDamage = 0;

    public AcidEffect() {
        super(MobEffectCategory.HARMFUL, 0X00FF00);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(!entity.level().isClientSide) {
            if(entity.getRandom().nextFloat() < 0.25F)
                AiUtil.sendParticlesInBox(entity.getBoundingBox(), NDMParticles.ACID_PARTICLE.get(), RandomSource.create().nextInt(3), (ServerLevel) entity.level(), entity.level().random);
            if(ticksTillDamage >= damageInterval) {
                entity.hurt(entity.damageSources().lava(), 1);
                ticksTillDamage = 0;
            }

            ticksTillDamage++;
        }

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
