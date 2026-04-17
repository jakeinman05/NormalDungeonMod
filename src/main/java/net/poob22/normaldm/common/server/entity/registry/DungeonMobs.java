package net.poob22.normaldm.common.server.entity.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.poob22.normaldm.common.client.model.FleshGuyModel;
import net.poob22.normaldm.common.client.model.maggot.ChargerMaggotModel;
import net.poob22.normaldm.common.client.render.entity.ChargerMaggotRenderer;
import net.poob22.normaldm.common.client.render.entity.GenericEntityRenderer;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;
import net.poob22.normaldm.common.server.entity.living.ChargerMaggotEntity;
import net.poob22.normaldm.common.server.entity.living.FleshGuyEntity;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class DungeonMobs {
    public static void init() {}

    public static ModelLayerLocation CHARGER_MAGGOT_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "charger_maggot"), "main");
    public static ModelLayerLocation FLESH_GUY_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "flesh_guy"), "main");
    public static ModelLayerLocation FLESH_BLOB_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "flesh_blob"), "main");

    //public static final DungeonMobDefinition<ChargerMaggotEntity> CHARGER_MAGGOT = DungeonMobRegistry.register(new DungeonMobDefinition<>("charger_maggot", ChargerMaggotEntity::new, MobCategory.MONSTER, 0.3F, 0.3F, ChargerMaggotEntity::createAttributes, CHARGER_MAGGOT_LAYER, ChargerMaggotModel::createBodyLayer, ctx -> new GenericEntityRenderer<>(ctx, new ChargerMaggotModel<>(ctx.bakeLayer(CHARGER_MAGGOT_LAYER)), 0.3F, ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/base.png"))));
    public static final DungeonMobDefinition<ChargerMaggotEntity> CHARGER_MAGGOT = DungeonMobRegistry.register(new DungeonMobDefinition<>("charger_maggot", ChargerMaggotEntity::new, MobCategory.MONSTER, 0.3F, 0.3F, ChargerMaggotEntity::createAttributes, CHARGER_MAGGOT_LAYER, ChargerMaggotModel::createBodyLayer, ChargerMaggotRenderer::new));
    public static final DungeonMobDefinition<FleshGuyEntity> FLESH_GUY = DungeonMobRegistry.register(new DungeonMobDefinition<>("flesh_guy", FleshGuyEntity::new, MobCategory.MONSTER, 0.6F, 1.7F, FleshGuyEntity::createAttributes, FLESH_GUY_LAYER, FleshGuyModel::createBodyLayer, ctx -> new GenericEntityRenderer<>(ctx, new FleshGuyModel<>(ctx.bakeLayer(FLESH_GUY_LAYER)), 0.5F, ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/flesh_guy.png"))));

}
