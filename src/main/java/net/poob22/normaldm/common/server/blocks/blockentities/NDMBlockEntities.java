package net.poob22.normaldm.common.server.blocks.blockentities;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.NDMBlocks;

public class NDMBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NormalDungeonMod.MODID);

    public static final RegistryObject<BlockEntityType<RoomControllerBlockEntity>> ROOM_CONTROLLER = BLOCK_ENTITY_TYPE.register("room_controller", () -> BlockEntityType.Builder.of(RoomControllerBlockEntity::new, NDMBlocks.ROOM_CONTROLLER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<DungeonMobSpawner>> DUNGEON_MOB_SPAWNER = BLOCK_ENTITY_TYPE.register("dungeon_mob_spawner", () -> BlockEntityType.Builder.of(DungeonMobSpawner::new, NDMBlocks.DUNGEON_MOB_SPAWNER_BLOCK.get()).build(null));

    public static void register(IEventBus bus) {BLOCK_ENTITY_TYPE.register(bus);}
}
