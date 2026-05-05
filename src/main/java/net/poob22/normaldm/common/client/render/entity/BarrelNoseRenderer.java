package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.BarrelNoseModel;
import net.poob22.normaldm.common.server.entity.living.BarrelNoseEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class BarrelNoseRenderer extends MobRenderer<BarrelNoseEntity, BarrelNoseModel<BarrelNoseEntity>> {
    ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/barrel_nose/base.png");
    ResourceLocation STUFFED = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/barrel_nose/stuffed.png");

    public BarrelNoseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BarrelNoseModel<>(renderManager.bakeLayer(DungeonMobs.BARREL_NOSE_LAYER)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BarrelNoseEntity entity) {
        return entity.isReloaded() ? STUFFED : BASE;
    }
}
