package net.poob22.normaldm.common.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.model.BioluminescentBeamSegmentModel;
import net.poob22.normaldm.common.server.entity.projectile.BioluminescentBeamEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class BaseBioLumBeamRenderer extends EntityRenderer<BioluminescentBeamEntity> {
    private final BioluminescentBeamSegmentModel<BioluminescentBeamEntity> model;

    private final int FRAME_TIME = 1;
    private static final ResourceLocation[] START_FRAMES = new ResourceLocation[] {
            rl("textures/entity/biolum_beam/beam_start0.png"),
            rl("textures/entity/biolum_beam/beam_start1.png"),
    };
    private static final ResourceLocation[] MIDDLE_FRAMES = new ResourceLocation[] {
            rl("textures/entity/biolum_beam/beam_middle0.png"),
            rl("textures/entity/biolum_beam/beam_middle1.png"),
            rl("textures/entity/biolum_beam/beam_middle2.png")
    };

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public BaseBioLumBeamRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new BioluminescentBeamSegmentModel<>(ctx.bakeLayer(DungeonMobs.BEAM_SEGMENT_LAYER));
    }

    @Override
    public void render(BioluminescentBeamEntity beam, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        if(beam.shooter != null && !beam.points.isEmpty() && !beam.pointso.isEmpty()) {
            int age = beam.tickCount;

            for(int i = 1; i < beam.points.size(); i++) {
                Vec3 start;
                Vec3 end;

                if(i < beam.pointso.size()) {
                    Vec3 oldStart = beam.pointso.get(i - 1);
                    Vec3 newStart = beam.points.get(i - 1);
                    Vec3 oldEnd = beam.pointso.get(i);
                    Vec3 newEnd = beam.points.get(i);

                    start = oldStart.lerp(newStart, partialTick);
                    end = oldEnd.lerp(newEnd, partialTick);

                    // snap render to position on first tick
                    if(beam.tickCount <= 2) {
                        start = beam.points.get(i - 1);
                        end = beam.points.get(i);
                    }

                } else {
                    continue;
                }

                Vec3 localStart = start.subtract(beam.position());
                Vec3 localEnd = end.subtract(beam.position());

                Vec3 dir = localEnd.subtract(localStart).normalize();

                float yaw = (float) Mth.atan2(dir.x,  dir.z) * Mth.RAD_TO_DEG;
                float pitch = (float) Mth.atan2(dir.y, Mth.sqrt((float)( Mth.square(dir.x) + Mth.square(dir.z)))) * Mth.RAD_TO_DEG;

                poseStack.pushPose();
                poseStack.translate(localStart.x, localStart.y, localStart.z);
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));

                if(beam.getLifetime() < 10) {
                    float scale = Mth.lerp(partialTick, beam.so, beam.s);
                    poseStack.scale(scale, scale, 1.0F);
                }

                int startSeed = i * 31 + beam.getId() * 17;
                int startOffset = Math.abs(startSeed) % START_FRAMES.length;
                int startIdx = ((age/FRAME_TIME) + startOffset) % START_FRAMES.length;

                int middleSeed = i * 31 + beam.getId() * 17;
                int middleOffset = Math.abs(middleSeed) % MIDDLE_FRAMES.length;
                int middleIdx = ((age/FRAME_TIME) + middleOffset) % MIDDLE_FRAMES.length;


                ResourceLocation texture;
                if(i == 1) texture = START_FRAMES[startIdx];
                else texture = MIDDLE_FRAMES[middleIdx];

                this.model.setupAnim(beam, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);
                VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(texture));
                this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
                VertexConsumer vertexConsumer0 = buffer.getBuffer(RenderType.eyes(texture));
                this.model.renderToBuffer(poseStack, vertexConsumer0, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);

                poseStack.popPose();
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BioluminescentBeamEntity beam) {
        return MIDDLE_FRAMES[0];
    }

    @Override
    public boolean shouldRender(BioluminescentBeamEntity beam, @NotNull Frustum camera, double pCamX, double pCamY, double pCamZ) {
        if(!beam.points.isEmpty()) {
            for(int i = 1; i < beam.points.size(); i++) {
                Vec3 start = beam.points.get(i - 1);
                Vec3 end = beam.points.get(i);
                AABB box = new AABB(start, end);
                if(camera.isVisible(box.inflate(1.0D))) {
                    return true;
                }
            }
        }
        return false;
    }
}
