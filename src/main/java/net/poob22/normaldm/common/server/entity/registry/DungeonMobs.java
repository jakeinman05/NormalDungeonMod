package net.poob22.normaldm.common.server.entity.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.common.client.model.BarrelNoseModel;
import net.poob22.normaldm.common.client.model.FleshBlobModel;
import net.poob22.normaldm.common.client.model.FleshGuyModel;
import net.poob22.normaldm.common.client.model.maggot.ChargerMaggotModel;
import net.poob22.normaldm.common.client.model.maggot.MaggotModel;
import net.poob22.normaldm.common.client.render.entity.*;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;
import net.poob22.normaldm.common.server.entity.living.*;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DungeonMobs {
    public static void init() {}

    public static ModelLayerLocation CHARGER_MAGGOT_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "charger_maggot"), "main");
    public static ModelLayerLocation FLESH_GUY_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "flesh_guy"), "main");
    public static ModelLayerLocation FLESH_BLOB_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "flesh_blob"), "main");
    public static ModelLayerLocation MAGGOT_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "maggot"), "main");
    public static ModelLayerLocation BARREL_NOSE_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "barrel_nose"), "main");

    public static ModelLayerLocation BASE_SHOT_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "base_shot"), "main");
    public static ModelLayerLocation SNOT_SHOT_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "snot_shot"), "main");

    //public static final DungeonMobDefinition<ChargerMaggotEntity> CHARGER_MAGGOT = DungeonMobRegistry.register(new DungeonMobDefinition<>("charger_maggot", ChargerMaggotEntity::new, MobCategory.MONSTER, 0.3F, 0.3F, ChargerMaggotEntity::createAttributes, CHARGER_MAGGOT_LAYER, ChargerMaggotModel::createBodyLayer, ctx -> new GenericEntityRenderer<>(ctx, new ChargerMaggotModel<>(ctx.bakeLayer(CHARGER_MAGGOT_LAYER)), 0.3F, ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/base.png"))));
    public static final DungeonMobDefinition<ChargerMaggotEntity> CHARGER_MAGGOT = DungeonMobRegistry.register(new DungeonMobDefinition<>("charger_maggot", ChargerMaggotEntity::new, MobCategory.MONSTER, 0.4F, 0.3F, ChargerMaggotEntity::createAttributes, CHARGER_MAGGOT_LAYER, ChargerMaggotModel::createBodyLayer, ChargerMaggotRenderer::new));
    public static final DungeonMobDefinition<FleshGuyEntity> FLESH_GUY = DungeonMobRegistry.register(new DungeonMobDefinition<>("flesh_guy", FleshGuyEntity::new, MobCategory.MONSTER, 0.6F, 1.5F, FleshGuyEntity::createAttributes, FLESH_GUY_LAYER, FleshGuyModel::createBodyLayer, FleshGuyRenderer::new));
    public static final DungeonMobDefinition<FleshBlobEntity> FLESH_BLOB = DungeonMobRegistry.register(new DungeonMobDefinition<>("flesh_blob", FleshBlobEntity::new, MobCategory.MONSTER, 0.6F, 0.5F, FleshBlobEntity::createAttributes, FLESH_BLOB_LAYER, FleshBlobModel::createBodyLayer, FleshBlobRenderer::new));
    public static final DungeonMobDefinition<MaggotEntity> MAGGOT = DungeonMobRegistry.register(new DungeonMobDefinition<>("maggot", MaggotEntity::new, MobCategory.MONSTER, 0.4F, 0.3F, MaggotEntity::createAttributes, MAGGOT_LAYER, MaggotModel::createBodyLayer, MaggotRenderer::new));
    public static final DungeonMobDefinition<BarrelNoseEntity> BARREL_NOSE = DungeonMobRegistry.register(new DungeonMobDefinition<>("barrel_nose", BarrelNoseEntity::new, MobCategory.MONSTER, 0.55F, 0.8125F, BarrelNoseEntity::createAttributes, BARREL_NOSE_LAYER, BarrelNoseModel::createBodyLayer, BarrelNoseRenderer::new));


    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            event.put((EntityType<? extends LivingEntity>) NDMEntities.get(def.id), def.attributes.get().build());
        }
    }

}
