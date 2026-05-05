package net.poob22.normaldm.common.client.events;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.poob22.normaldm.common.client.model.BaseShotModel;
import net.poob22.normaldm.common.client.particles.*;
import net.poob22.normaldm.common.client.render.entity.BaseProjectileRenderer;
import net.poob22.normaldm.common.server.blocks.NDMBlocks;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobRegistry;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            registerRenderer(def);
        }

        // other entities
        EntityRenderers.register(NDMEntities.FLESH_SHOT.get(), (ctx) -> new BaseProjectileRenderer<>(ctx, new BaseShotModel<>(ctx.bakeLayer(DungeonMobs.BASE_SHOT_LAYER)), ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/base_shot.png")));
        EntityRenderers.register(NDMEntities.SNOT_SHOT.get(), (ctx) -> new BaseProjectileRenderer<>(ctx, new BaseShotModel<>(ctx.bakeLayer(DungeonMobs.SNOT_SHOT_LAYER)), ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/snot_shot.png")));

        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(NDMBlocks.DUNGEON_MOB_SPAWNER_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(NDMBlocks.CELLAR_GATE.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent .RegisterLayerDefinitions event) {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            event.registerLayerDefinition(def.layerLocation, def.layerDefinition);
        }

        // other entities
        event.registerLayerDefinition(DungeonMobs.BASE_SHOT_LAYER, BaseShotModel::createBodyLayer);
        event.registerLayerDefinition(DungeonMobs.SNOT_SHOT_LAYER,  BaseShotModel::createBodyLayer);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Mob> void registerRenderer(DungeonMobDefinition<T> def) {
        EntityRenderers.register((EntityType<T>) NDMEntities.get(def.id), (EntityRendererProvider<? super T>) def.renderer);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NDMParticles.BLOOD_POOL.get(), BloodPoolParticle.Factory::new);
        event.registerSpriteSet(NDMParticles.HURT_PARTICLE.get(), HurtParticle.Factory::new);
        event.registerSpriteSet(NDMParticles.FLESH_PARTICLE.get(), FleshParticle.Factory::new);
        event.registerSpriteSet(NDMParticles.SNOT_PARTICLE.get(), SnotParticle.Factory::new);
    }
}
