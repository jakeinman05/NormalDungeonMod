package net.poob22.normaldm.common.server.combat.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class CombatInternalCapabilities {
    public static final Capability<PlayerCombatCapability> COMBAT = CapabilityManager.get(new CapabilityToken<>() {});

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
            var entity = event.getObject();

            if(entity instanceof Player) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(MODID, "player_dungeon_combat"), new CombatCapabilityProvider());
            }
        }
    }

    @Mod.EventBusSubscriber
    public static class PlayerCapabilityHandler {
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            Player player = event.player;
            // only run on sever end phase
            if(event.phase != TickEvent.Phase.END) {
                return;
            }
            if(player.level().isClientSide()) { // && toggle for showing dev gui is ON
                player.getCapability(COMBAT).ifPresent(cap -> {
                    cap.tick(player);
                });
            } else {
                player.getCapability(COMBAT).ifPresent(cap -> {
                    cap.tick(player);
                });
            }
        }
    }

    public static class CombatCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
        private final PlayerCombatCapability data = new PlayerCombatCapability();

        private final LazyOptional<PlayerCombatCapability> optional = LazyOptional.of(() -> data);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return optional.cast();
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            data.deserializeNBT(tag);
        }
    }
}
