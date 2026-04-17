package net.poob22.normaldm.common.server.entity.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributesEvents {
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            event.put((EntityType<? extends LivingEntity>) NDMEntities.get(def.id), def.attributes.get().build());
        }
    }
}
