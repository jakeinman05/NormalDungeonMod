package net.poob22.normaldm.common.server.entity.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.poob22.normaldm.common.client.model.maggot.ChargerMaggotModel;
import net.poob22.normaldm.common.client.render.entity.ChargerMaggotRenderer;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;
import net.poob22.normaldm.common.server.entity.living.ChargerMaggotEntity;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class DungeonMobs {
    public static ModelLayerLocation CHARGER_MAGGOT_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "charger_maggot"), "main");

    //public static final DungeonMobDefinition<ChargerMaggotEntity> CHARGER_MAGGOT = DungeonMobRegistry.register(new DungeonMobDefinition<>("charger_maggot", ChargerMaggotEntity::new, MobCategory.MONSTER, 0.3F, 0.3F, ChargerMaggotEntity::createAttributes, CHARGER_MAGGOT_LAYER, ChargerMaggotModel::createBodyLayer, ctx -> new GenericMobRenderer<>(ctx, new ChargerMaggotModel<>(ctx.bakeLayer(CHARGER_MAGGOT_LAYER)), 0.3F, ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/base.png"))));
    public static final DungeonMobDefinition<ChargerMaggotEntity> CHARGER_MAGGOT = DungeonMobRegistry.register(new DungeonMobDefinition<>("charger_maggot", ChargerMaggotEntity::new, MobCategory.MONSTER, 0.3F, 0.3F, ChargerMaggotEntity::createAttributes, CHARGER_MAGGOT_LAYER, ChargerMaggotModel::createBodyLayer, ChargerMaggotRenderer::new));

    public static void init() {}
}
