package net.poob22.normaldm.common.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.packet.CombatDebugPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.NormalDungeonMod.MODID;
import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        NormalDungeonMod.LOGGER.info("Registering Commands");
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("combat")
                /// DEBUG
                .then(
                        Commands.literal("showDebug")
                                .executes(ctx -> {
                                    ServerPlayer player = getPlayerFrom(ctx);

                                    player.getCapability(COMBAT).ifPresent(c -> {
                                        c.toggleDebug();
                                        PacketHandler.sendToTracking(player, new CombatDebugPacket(c.showDebug()));

                                        ctx.getSource().sendSuccess(() -> Component.literal("Combat Debug " + (c.showDebug() ? "Enabled" : "Disabled")), false);
                                    });

                                    return 1;
                                })
                )

                /// STAT MANIPULATORS
                .then(Commands.literal("stats")

                        .then(Commands.literal("set")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests(
                                            (ctx, builder) -> {
                                                for(StatType statType : StatType.values()) {
                                                    builder.suggest(statType.name().toLowerCase());
                                                }
                                                return builder.buildFuture();
                                            })
                                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                .executes(
                                                        ctx -> {
                                                            String stat = StringArgumentType.getString(ctx, "type");
                                                            float value = FloatArgumentType.getFloat(ctx, "value");

                                                            ServerPlayer player = getPlayerFrom(ctx);
                                                            player.getCapability(COMBAT).ifPresent(c -> {
                                                                StatType type = StatType.valueOf(stat.toUpperCase());
                                                                c.getStats().setStat(type, value);
                                                            });

                                                            return 1;
                                                        })
                                        )
                                )
                        )

                        .then(Commands.literal("add")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests(
                                            (ctx, builder) -> {
                                                for(StatType statType : StatType.values()) {
                                                    builder.suggest(statType.name().toLowerCase());
                                                }
                                                return builder.buildFuture();
                                            }
                                        )
                                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                .executes(ctx -> {
                                                    String stat = StringArgumentType.getString(ctx, "type");
                                                    float value = FloatArgumentType.getFloat(ctx, "value");

                                                    ServerPlayer player = getPlayerFrom(ctx);

                                                    player.getCapability(COMBAT).ifPresent(c -> {
                                                        StatType type = StatType.valueOf(stat.toUpperCase());
                                                        c.getStats().modifyStat(type, value);
                                                    });

                                                    return 1;
                                                })
                                        )
                                )
                        )

                        .then(Commands.literal("reset")
                                .executes(ctx -> {
                                    ServerPlayer player = getPlayerFrom(ctx);

                                    player.getCapability(COMBAT).ifPresent(c -> {
                                        c.getStats().resetStats();
                                    });

                                    return 1;
                                }))
                )


        );
    }

    public static ServerPlayer getPlayerFrom(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ctx.getSource().getPlayerOrException();
    }
}
