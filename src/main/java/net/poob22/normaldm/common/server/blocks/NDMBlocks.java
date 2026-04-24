package net.poob22.normaldm.common.server.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.items.NDMItems;

import java.util.function.Supplier;

public class NDMBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NormalDungeonMod.MODID);

    public static final RegistryObject<Block> ROOM_CONTROLLER_BLOCK = registerBlockWithItem("room_controller", RoomControllerBlock::new);
    public static final RegistryObject<Block> PLYWOOD = registerBlockWithItem("plywood", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> CELLAR_WALL = registerBlockWithItem("cellar_wall", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CELLAR_WALL_DRAIN = registerBlockWithItem("cellar_wall_drain", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CELLAR_WALL_CRACKED = registerBlockWithItem("cellar_wall_cracked", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CELLAR_WALL_MOSSY = registerBlockWithItem("cellar_wall_mossy", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CELLAR_CEILING = registerBlockWithItem("cellar_ceiling", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CELLAR_CEILING_RIPPED = registerBlockWithItem("cellar_ceiling_ripped", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CELLAR_CEILING_MOLDY = registerBlockWithItem("cellar_ceiling_moldy", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CELLAR_CEILING_FLESH_TORN = registerBlockWithItem("cellar_ceiling_flesh_torn", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> FLESH_BLOCK = registerBlockWithItem("flesh_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SCULK)));


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
