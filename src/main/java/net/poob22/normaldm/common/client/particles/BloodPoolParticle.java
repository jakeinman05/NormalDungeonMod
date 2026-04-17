package net.poob22.normaldm.common.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BloodPoolParticle extends TextureSheetParticle {
    private final float rotation;

    protected BloodPoolParticle(ClientLevel level, double x, double y, double z, float size) {
        super(level, x, y, z);

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.quadSize = size;
        this.lifetime = 600;

        this.gravity = 0;
        this.hasPhysics = false;

        this.alpha = 1.0F;

        this.roll = 0;
        this.oRoll = 0;
        this.rotation = this.random.nextFloat() * Mth.TWO_PI;
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float)this.age / (float)this.lifetime;
        this.alpha = 1.0F - (progress * progress);


    }

    @Override
    public void render(VertexConsumer buffer, Camera cameraInfo, float partialTicks) {
        Vec3 camPos = cameraInfo.getPosition();

        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x);
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y) + 0.01F;
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z);

        float size = this.getQuadSize(partialTicks);
        float cos = Mth.cos(this.rotation);
        float sin = Mth.sin(this.rotation);

        float[][] corners = {
                {-size, -size},
                {-size,  size},
                { size,  size},
                { size, -size}
        };

        float[] xs = new float[4];
        float[] zs = new float[4];

        for(int i = 0; i < 4; i++){
            float cx = corners[i][0];
            float cz = corners[i][1];

            xs[i] = cx * cos - cz * sin;
            zs[i] = cx * sin + cz * cos;
        }
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        int light = this.getLightColor(partialTicks);

        buffer.vertex(x + xs[0], y, z + zs[0]).uv(u1, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(x + xs[1], y, z + zs[1]).uv(u1, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(x + xs[2], y, z + zs[2]).uv(u0, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(x + xs[3], y, z + zs[3]).uv(u0, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            float size = (float) xSpeed;
            BloodPoolParticle particle = new BloodPoolParticle(level, x, y, z, size);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}


