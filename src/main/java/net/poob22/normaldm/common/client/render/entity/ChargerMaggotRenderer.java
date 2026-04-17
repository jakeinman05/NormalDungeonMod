package net.poob22.normaldm.common.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.poob22.normaldm.common.client.model.maggot.ChargerMaggotModel;
import net.poob22.normaldm.common.server.entity.living.ChargerMaggotEntity;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class ChargerMaggotRenderer extends MobRenderer<ChargerMaggotEntity, ChargerMaggotModel<ChargerMaggotEntity>> {
    ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/charger_maggot/base.png");
    ResourceLocation CHARGING = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/charger_maggot/charging.png");

    public ChargerMaggotRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChargerMaggotModel<>(renderManager.bakeLayer(DungeonMobs.CHARGER_MAGGOT_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(ChargerMaggotEntity maggot) {
        return maggot.isCharging() ? CHARGING : BASE;
    }
}
