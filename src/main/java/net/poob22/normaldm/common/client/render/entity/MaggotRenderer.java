package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.maggot.MaggotModel;
import net.poob22.normaldm.common.server.entity.living.MaggotEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class MaggotRenderer extends MobRenderer<MaggotEntity, MaggotModel<MaggotEntity>> {
    public MaggotRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MaggotModel<>(ctx.bakeLayer(DungeonMobs.MAGGOT_LAYER)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(MaggotEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/maggot.png");
    }
}
