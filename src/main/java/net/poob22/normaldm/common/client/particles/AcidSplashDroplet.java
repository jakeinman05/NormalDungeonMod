package net.poob22.normaldm.common.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class AcidSplashDroplet extends TextureSheetParticle {
    int onGroundTime = 0;

    protected AcidSplashDroplet(ClientLevel pLevel, double pX, double pY, double pZ, double sX, double sY, double sZ, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, sX, sY, sZ);

        this.xd *= 0.8D;
        this.yd *= 0.8D;
        this.zd *= 0.8D;

        this.hasPhysics = true;
        this.gravity = 1.0F;
        this.friction = 0.96F;
        this.lifetime = 100;
        this.quadSize = (float) (0.05F + (random.nextDouble() * 0.01));
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        if(this.onGround) {
            this.onGroundTime++;
        }

        if(this.onGroundTime >= 55 + random.nextInt(10)) {
            this.remove();
        }
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
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xs, double ys, double zs) {
            return new AcidSplashDroplet(level, x, y, z, xs, ys, zs, sprite);
        }
    }
}
