package net.poob22.normaldm.common.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FleshParticle extends TextureSheetParticle {

    protected FleshParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ, 0.0F, 0.0F, 0.0F);
        this.xd *= 0.8F;
        this.yd *= 0.8F;
        this.zd *= 0.8F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.quadSize *= 0.7F + random.nextFloat();
        this.friction = 0.96F;
        this.gravity = 1.0F;
        this.hasPhysics = true;
        this.lifetime = 160 + random.nextInt(40);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            FleshParticle particle = new FleshParticle(pLevel, pX, pY, pZ);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
