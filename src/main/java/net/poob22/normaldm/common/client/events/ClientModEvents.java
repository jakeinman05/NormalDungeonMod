package net.poob22.normaldm.common.client.events;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.poob22.normaldm.common.client.particles.BloodPoolParticle;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobRegistry;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            registerRenderer(def);
        }
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent .RegisterLayerDefinitions event) {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            event.registerLayerDefinition(def.layerLocation, def.layerDefinition);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Mob> void registerRenderer(DungeonMobDefinition<T> def) {
        EntityRenderers.register((EntityType<T>) NDMEntities.get(def.id), def.renderer);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NDMParticles.BLOOD_POOL.get(), BloodPoolParticle.Factory::new);
    }
}
