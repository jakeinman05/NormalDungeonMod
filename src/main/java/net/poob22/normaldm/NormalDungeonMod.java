package net.poob22.normaldm;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.blocks.NDMBlocks;
import net.poob22.normaldm.common.server.blocks.blockentities.NDMBlockEntities;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;
import net.poob22.normaldm.common.server.items.NDMItems;
import org.slf4j.Logger;

@Mod(NormalDungeonMod.MODID)
public class NormalDungeonMod
{
    public static final String MODID = "normaldm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NormalDungeonMod.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TAB.register("normaldm_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(NDMBlocks.CELLAR_WALL.get()))
            .title(Component.literal("Normal Dungeon Mod"))
            .displayItems(((parameters, output) -> {
                output.accept(NDMBlocks.CELLAR_WALL.get());
                output.accept(NDMBlocks.CELLAR_WALL_DRAIN.get());
                output.accept(NDMBlocks.CELLAR_WALL_CRACKED.get());
                output.accept(NDMBlocks.CELLAR_WALL_MOSSY.get());
                output.accept(NDMBlocks.CELLAR_CEILING.get());
                output.accept(NDMBlocks.CELLAR_CEILING_RIPPED.get());
                output.accept(NDMBlocks.CELLAR_CEILING_MOLDY.get());
                output.accept(NDMBlocks.CELLAR_CEILING_FLESH_TORN.get());
                output.accept(NDMBlocks.PLYWOOD.get());
                output.accept(NDMBlocks.FLESH_BLOCK.get());
                output.accept(NDMBlocks.CELLAR_GATE.get());
                output.accept(NDMBlocks.ROOM_CONTROLLER_BLOCK.get());
                output.accept(NDMBlocks.DUNGEON_MOB_SPAWNER_BLOCK.get());
                output.accept(NDMItems.MAGGOT_SPAWN_EGG.get());
                output.accept(NDMItems.CHARGER_MAGGOT_SPAWN_EGG.get());
                output.accept(NDMItems.FLESH_GUY_SPAWN_EGG.get());
                output.accept(NDMItems.BARREL_NOSE_SPAWN_EGG.get());
                output.accept(NDMItems.DUNGEON_WAND.get());
            }))
            .build());

    public NormalDungeonMod(FMLJavaModLoadingContext context)
    {
        IEventBus bus = context.getModEventBus();

        DungeonMobs.init();
        NDMEntities.RegisterAll();
        NDMEntities.register(bus);
        NDMBlocks.register(bus);
        NDMItems.register(bus);
        NDMBlockEntities.register(bus);
        NDMParticles.register(bus);
        PacketHandler.registerPackets();
        CREATIVE_MODE_TAB.register(bus);

        bus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
}
