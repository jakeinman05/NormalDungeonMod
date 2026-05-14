package net.poob22.normaldm.common.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.crescent.CrescentModel;
import net.poob22.normaldm.common.client.model.crescent.TallCrescentModel;
import net.poob22.normaldm.common.server.entity.living.CrescentEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class CrescentRenderer extends MobRenderer<CrescentEntity, HierarchicalModel<CrescentEntity>> {
    ResourceLocation SHORT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/crescent/crescent.png");
    ResourceLocation TALL = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/crescent/crescent_tall.png");

    CrescentModel<CrescentEntity> shortModel;
    TallCrescentModel<CrescentEntity> tallModel;

    public CrescentRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CrescentModel<>(ctx.bakeLayer(DungeonMobs.CRESCENT_LAYER)), 0.3F);

        this.shortModel = new CrescentModel<>(ctx.bakeLayer(DungeonMobs.CRESCENT_LAYER));
        this.tallModel = new TallCrescentModel<>(ctx.bakeLayer(DungeonMobs.TALL_CRESCENT_LAYER));
    }

    @Override
    public void render(@NotNull CrescentEntity pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        this.model = pEntity.isTall() ? tallModel : shortModel;

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CrescentEntity pEntity) {
        return pEntity.isTall() ? TALL : SHORT;
    }
}
