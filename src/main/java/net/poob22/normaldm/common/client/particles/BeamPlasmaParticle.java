package net.poob22.normaldm.common.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.poob22.normaldm.NormalDungeonMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeamPlasmaParticle extends TextureSheetParticle {
    private int onGroundTime;

    float startR;
    float startG;
    float startB;
    float endR = 0.388F;
    float endG = 0.149F;
    float endB = 0.0F;

    protected BeamPlasmaParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);

        this.friction = 0.96F;
        this.gravity = 1.0F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= 0.8;
        this.yd *= 0.8;
        this.zd *= 0.8;
        this.quadSize *= 0.5F + (random.nextFloat() - 0.5F) * 0.05F;
        this.lifetime = 40 + random.nextInt(40);
        this.lifetime = Math.max(this.lifetime, 1);
        this.hasPhysics = true;

        this.startR = this.rCol;
        this.startG = this.gCol;
        this.startB = this.bCol;
    }

    @Override
    public void tick() {
        super.tick();

        if(this.onGround) {
            this.xd *= 0.3D;
            this.yd *= -0.3D;
            this.zd *= 0.3D;

            this.onGroundTime++;
        }

        float progress = (float)this.age / (float)this.lifetime;
        progress = progress * progress;
        float r = Mth.lerp(progress, this.startR, this.endR);
        float g = Mth.lerp(progress, this.startG, this.endG);
        float b = Mth.lerp(progress, this.startB, this.endB);
        this.setColor(r, g, b);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        int packed = super.getLightColor(pPartialTick);
        int sky = packed >> 16 & 255;
        float p = (float) this.age /this.lifetime;
        p = p * p;
        int block = (int)Mth.lerp(p, 240, 96);

        return block | (sky << 16);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            BeamPlasmaParticle particle = new BeamPlasmaParticle(pLevel, pX, pY, pZ);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
