package net.poob22.normaldm.common.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.common.client.packet.EffectInstanceSyncPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.packet.combat.PlayerHoldingLClickPacket;
import net.poob22.normaldm.common.server.mobeffects.NDMEffects;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void checkPlayerClick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if(mc.player == null) return;

        if(mc.options.keyAttack.isDown()) {
            LocalPlayer player = mc.player;

            if(checkLevel(player)) {
                Vec3 v = player.getDeltaMovement();
                PacketHandler.sendToServer(new PlayerHoldingLClickPacket(v));
            }
        }
    }

    private static boolean checkLevel(Player player) {
        ItemStack stack = player.getMainHandItem();
        ResourceLocation dim = player.level().dimension().location();
        return stack.is(ItemStack.EMPTY.getItem()) && dim.getNamespace().equals(MODID);
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class EffectSyncEvents {
        @SubscribeEvent
        public static void onEffectAdded(MobEffectEvent.Added event) {
            if (!event.getEntity().level().isClientSide && event.getEffectInstance().getEffect() == NDMEffects.ACID.get()) {
                sync(event.getEntity(), true);
            }
        }

        @SubscribeEvent
        public static void onEffectExpired(MobEffectEvent.Expired event) {
            if (!event.getEntity().level().isClientSide && event.getEffectInstance().getEffect() == NDMEffects.ACID.get()) {
                sync(event.getEntity(), false);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onEffectRemoved(MobEffectEvent.Remove event) {
            if (!event.getEntity().level().isClientSide && !event.isCanceled() && event.getEffect() == NDMEffects.ACID.get()) {
                sync(event.getEntity(), false);
            }
        }

        private static void sync(LivingEntity entity, boolean active) {
            PacketHandler.sendToTracking(entity, new EffectInstanceSyncPacket(entity.getUUID(), active));
        }
    }
}
