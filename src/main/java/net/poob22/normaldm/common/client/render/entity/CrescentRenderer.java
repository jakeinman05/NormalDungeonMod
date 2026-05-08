package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.CrescentModel;
import net.poob22.normaldm.common.server.entity.living.CrescentEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class CrescentRenderer extends MobRenderer<CrescentEntity, CrescentModel<CrescentEntity>> {
    ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/crescent.png");

    public CrescentRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CrescentModel<>(ctx.bakeLayer(DungeonMobs.CRESCENT_LAYER)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CrescentEntity pEntity) {
        return BASE;
    }
}
