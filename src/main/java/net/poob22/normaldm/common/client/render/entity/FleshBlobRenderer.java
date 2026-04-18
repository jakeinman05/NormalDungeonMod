package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.FleshBlobModel;
import net.poob22.normaldm.common.server.entity.living.FleshBlobEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class FleshBlobRenderer extends MobRenderer<FleshBlobEntity, FleshBlobModel<FleshBlobEntity>> {
    public FleshBlobRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FleshBlobModel<>(ctx.bakeLayer(DungeonMobs.FLESH_BLOB_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(FleshBlobEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/flesh_guy.png");
    }
}
