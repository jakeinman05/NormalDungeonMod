package net.poob22.normaldm.common.server.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.items.NDMItems;

import java.util.function.Supplier;

public class NDMBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NormalDungeonMod.MODID);

    public static final RegistryObject<Block> ROOM_CONTROLLER_BLOCK = registerBlockWithItem("room_controller", () -> new RoomControllerBlock());

    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        RegistryObject<T> blockRegistry = BLOCKS.register(name, block);
        registerBlockItem(name, blockRegistry);
        return blockRegistry;
    }

    private static <T extends Block> RegistryObject<T> registerRareBlockWithItem(String name, Supplier<T> block) {
        RegistryObject<T> blockRegistry = BLOCKS.register(name, block);
        registerRareBlockItem(name, blockRegistry);
        return blockRegistry;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String id, RegistryObject<T> block) {
        return NDMItems.ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<Item> registerRareBlockItem(String id, RegistryObject<T> block) {
        return NDMItems.ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties().rarity(Rarity.EPIC)));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
