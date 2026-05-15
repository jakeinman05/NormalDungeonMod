package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.FleshGuyModel;
import net.poob22.normaldm.common.server.entity.living.FleshGuyEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class FleshGuyRenderer extends MobRenderer<FleshGuyEntity, FleshGuyModel<FleshGuyEntity>> {
    ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/flesh_guy/flesh_guy.png");
    ResourceLocation SLOW = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/flesh_guy/flesh_guy_slow.png");
    ResourceLocation SEE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/flesh_guy/flesh_guy_see.png");
    ResourceLocation FAST = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/flesh_guy/flesh_guy_fast.png");

    public FleshGuyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FleshGuyModel<>(ctx.bakeLayer(DungeonMobs.FLESH_GUY_LAYER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(FleshGuyEntity pEntity) {
        return switch(pEntity.getTypeInt()) {
            case 0 -> BASE;
            case 1 -> SLOW;
            case 2 -> FAST;
            case 3 -> SEE;
            default -> throw new IllegalStateException("Unexpected value: " + pEntity.getTypeInt());
        };
    }
}
