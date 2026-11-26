package net.poob22.normaldm.common.server.mobeffects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.entity.ai.AiUtil;

public class AcidEffect extends MobEffect {
    private static final int damageInterval = 10;

    public AcidEffect() {
        super(MobEffectCategory.HARMFUL, 0X00FF00);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(!entity.level().isClientSide) {
            RandomSource random = entity.getRandom();
            if(random.nextFloat() < 0.25F) {
                AiUtil.sendParticlesInBox(entity.getBoundingBox(), NDMParticles.ACID_PARTICLE.get(), random.nextInt(3), (ServerLevel) entity.level(), random);
            }
            if(entity.tickCount % damageInterval == 0) {
                entity.hurt(entity.damageSources().lava(), 1);
            }
        }

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
