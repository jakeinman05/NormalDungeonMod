package net.poob22.normaldm.common.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.poob22.normaldm.common.client.render.state.ClientEffectState;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class AcidOverlayLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation ACID_OVERLAY = ResourceLocation.fromNamespaceAndPath(MODID, "textures/overlay/acid_overlay.png");

    public AcidOverlayLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        if(!ClientEffectState.hasAcid(entity)) {
            return;
        }

        M model = this.getParentModel();

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(ACID_OVERLAY));
        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.4F);
    }
}
