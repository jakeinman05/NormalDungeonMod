package net.poob22.normaldm.common.server.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> DUNGEON_WAND = ITEMS.register("dungeon_wand", () -> new DungeonWandItem(new Item.Properties()));

    public static final RegistryObject<Item> MAGGOT_SPAWN_EGG = ITEMS.register("maggot_spawn_egg", () -> new DungeonMobSpawnEgg(DungeonMobs.MAGGOT.entityType, 0, 0, new Item.Properties()));
    public static final RegistryObject<Item> CHARGER_MAGGOT_SPAWN_EGG = ITEMS.register("charger_maggot_egg", () -> new DungeonMobSpawnEgg(DungeonMobs.CHARGER_MAGGOT.entityType, 0, 0, new Item.Properties()));
    public static final RegistryObject<Item> FLESH_GUY_SPAWN_EGG = ITEMS.register("flesh_guy_egg", () -> new DungeonMobSpawnEgg(DungeonMobs.FLESH_GUY.entityType, 0, 0, new Item.Properties()));
    public static final RegistryObject<Item> BARREL_NOSE_SPAWN_EGG = ITEMS.register("barrel_nose_egg", () -> new DungeonMobSpawnEgg(DungeonMobs.BARREL_NOSE.entityType, 0, 0, new Item.Properties()));

    public static void register(IEventBus bus) {ITEMS.register(bus);}
}
