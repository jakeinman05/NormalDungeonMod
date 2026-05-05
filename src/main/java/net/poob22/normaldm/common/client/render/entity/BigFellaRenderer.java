package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.BigFellaModel;
import net.poob22.normaldm.common.server.entity.living.BigFellaEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class BigFellaRenderer extends MobRenderer<BigFellaEntity, BigFellaModel<BigFellaEntity>> {
    public BigFellaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BigFellaModel<>(ctx.bakeLayer(DungeonMobs.BIG_FELLA_LAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(BigFellaEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/big_fella.png");
    }
}
