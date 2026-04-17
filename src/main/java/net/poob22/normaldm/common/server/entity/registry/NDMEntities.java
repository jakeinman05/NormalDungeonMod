package net.poob22.normaldm.common.server.entity.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;

import java.util.HashMap;
import java.util.Map;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    private static final Map<String, RegistryObject<EntityType<?>>> ENTITY_MAP = new HashMap<>();

    public static void RegisterAll() {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            RegistryObject<EntityType<?>> reg = ENTITY_TYPES.register(def.id, () -> EntityType.Builder.of(def.factory, def.mobCategory).sized(def.width, def.height).build(def.id));
            ENTITY_MAP.put(def.id, reg);
        }
    }

    public static EntityType<?> get(String id) {
        return ENTITY_MAP.get(id).get();
    }

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
