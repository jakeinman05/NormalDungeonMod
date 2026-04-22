package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.FleshGuyModel;
import net.poob22.normaldm.common.server.entity.living.FleshGuyEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class FleshGuyRenderer extends MobRenderer<FleshGuyEntity, FleshGuyModel<FleshGuyEntity>> {
    public FleshGuyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FleshGuyModel<>(ctx.bakeLayer(DungeonMobs.FLESH_GUY_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(FleshGuyEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/flesh_guy.png");
    }
}
