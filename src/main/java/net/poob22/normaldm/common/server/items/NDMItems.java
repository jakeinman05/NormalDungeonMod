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

    public static final RegistryObject<Item> MAGGOT_SPAWN_EGG = ITEMS.register("maggot_spawn_egg", () -> new DungeonMobSpawnEgg(DungeonMobs.MAGGOT.entityType, 0, 0, new Item.Properties()));

    public static void register(IEventBus bus) {ITEMS.register(bus);}
}
