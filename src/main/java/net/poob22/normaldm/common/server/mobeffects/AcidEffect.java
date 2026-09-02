package net.poob22.normaldm.common.server.mobeffects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.poob22.normaldm.common.server.entity.ai.AiUtil;

public class AcidEffect extends MobEffect {
    protected AcidEffect() {
        super(MobEffectCategory.HARMFUL, 0X00FF00);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().lava(), 1);
        if(!entity.level().isClientSide) {
            AiUtil.sendParticlesInBox(entity.getBoundingBox(), ParticleTypes.SMOKE, 8, (ServerLevel) entity.level(), entity.level().random);
        }

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % (30 - (amplifier * 2)) == 0;
    }
}
