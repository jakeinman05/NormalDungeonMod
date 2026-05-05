package net.poob22.normaldm.common.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SnotParticle extends HurtParticle {
    protected SnotParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites);
    }

    @Override
    public void tick() {
        if(this.onGround) {
            this.yd *= -0.3;
            this.xd *= 0.7;
            this.zd *= 0.7;
        }

        Vec3 vec3 = new Vec3(this.xd, this.yd, this.zd);
        if(vec3.equals(Vec3.ZERO)) {
            this.yd *= 0.01;
            this.xd = 0.0;
            this.zd = 0.0;
        }

        super.tick();
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new SnotParticle(pLevel, pX, pY, pZ, this.sprites);
        }
    }
}
