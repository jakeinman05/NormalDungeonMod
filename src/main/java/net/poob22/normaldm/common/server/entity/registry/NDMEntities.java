package net.poob22.normaldm.common.server.entity.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;
import net.poob22.normaldm.common.server.entity.projectile.FleshShotEntity;

import java.util.HashMap;
import java.util.Map;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    private static final Map<String, RegistryObject<EntityType<?>>> ENTITY_MAP = new HashMap<>();

    public static final RegistryObject<EntityType<FleshShotEntity>> FLESH_SHOT = ENTITY_TYPES.register("flesh_shot", () -> EntityType.Builder.of(FleshShotEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build("flesh_shot"));

    public static void RegisterAll() {
        for(DungeonMobDefinition<?> def : DungeonMobRegistry.MOBS) {
            registerSingle(def);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Mob> void registerSingle(DungeonMobDefinition<T> def) {
        RegistryObject<EntityType<T>> reg = ENTITY_TYPES.register(def.id, () -> EntityType.Builder.of(def.factory, def.mobCategory).sized(def.width, def.height).build(def.id));
        def.entityType = reg;
        ENTITY_MAP.put(def.id, (RegistryObject<EntityType<?>>)(RegistryObject<?>) reg);
    }

    public static EntityType<?> get(String id) {
        return ENTITY_MAP.get(id).get();
    }

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
