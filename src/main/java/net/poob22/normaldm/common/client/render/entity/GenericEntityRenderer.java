package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class GenericEntityRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    private final ResourceLocation texture;

    public GenericEntityRenderer(EntityRendererProvider.Context ctx, M model, float shadowSize, ResourceLocation texture) {
        super(ctx, model, shadowSize);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return texture;
    }
}
