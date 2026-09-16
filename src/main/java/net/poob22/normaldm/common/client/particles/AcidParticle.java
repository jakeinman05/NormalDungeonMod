package net.poob22.normaldm.common.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AcidParticle extends TextureSheetParticle {
    boolean hasHitGround = false;
    int onGroundTime = 0;
    SpriteSet sprites;

    protected AcidParticle(ClientLevel pLevel, double pX, double pY, double pZ, double sX, double yS, double zS, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ);

        this.xd *= 0.8D;
        this.yd *= 0.8D;
        this.zd *= 0.8D;

        this.hasPhysics = true;
        this.gravity = 1.0F;
        this.friction = 0.98F;
        this.lifetime = 200;
        this.quadSize = 0.16F;

        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        if(this.onGround) {
            if(!hasHitGround) {
                hasHitGround = true;
                for(int i = 0; i < random.nextInt(3); i++)
                    this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, 0, 0, 0);
                for(int i = 0; i < random.nextInt(4); i++)
                    this.level.addParticle(NDMParticles.ACID_SPLASH_DROPLET.get(), this.x, this.y, this.z, -0.1 + random.nextDouble() * 0.1, 0.15 + random.nextDouble() * 0.15, -0.1 + random.nextDouble() * 0.1);
            }

            this.onGroundTime++;
            if(this.onGroundTime >= 100) {
                this.remove();
            }
        }

        int sprite = this.onGround ? 1 : 0;
        this.setSprite(sprites.get(sprite, 1));

    }

    @Override
    protected int getLightColor(float pPartialTick) {
        int packed = super.getLightColor(pPartialTick);
        int sky = packed >> 16 & 255;
        float p = (float) this.age /this.lifetime;
        p = p * p;
        int block = (int) Mth.lerp(p, 240, 96);

        return block | (sky << 16);
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
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xs, double ys, double zs) {
            return new AcidParticle(level, x, y, z, xs, ys, zs, this.sprites);
        }
    }
}
