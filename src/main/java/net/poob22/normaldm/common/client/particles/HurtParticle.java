package net.poob22.normaldm.common.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class HurtParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int onGroundTime;

    protected HurtParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);

        this.friction = 0.96F;
        this.gravity = 1.0F;
        this.sprites = sprites;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= 0.8;
        this.yd *= 0.8;
        this.zd *= 0.8;
        this.quadSize *= 0.7F + random.nextFloat();
        this.lifetime = 160 + random.nextInt(40);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
    }

    @Override
    public void tick() {
        if(this.onGround) {
            onGroundTime++;
        }

        int sprite = this.onGround ? 1 : 0;
        this.setSprite(sprites.get(sprite, 1));

        if(this.onGroundTime >= 60) this.remove();

        super.tick();
    }

    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float) this.age + f) / (float) this.lifetime * 24.0F, 0.0F, 1.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xs, double ys, double zs) {
            return new HurtParticle(level, x, y, z, this.sprites);
        }
    }
}
