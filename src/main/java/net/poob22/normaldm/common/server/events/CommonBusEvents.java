package net.poob22.normaldm.common.server.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.poob22.normaldm.common.client.notifications.ItemPickupNotification;
import net.poob22.normaldm.common.client.notifications.NotificationManager;
import net.poob22.normaldm.common.client.packet.EffectInstanceSyncPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.server.items.stat_modifiers.StatItem;
import net.poob22.normaldm.common.server.misc.NDMTagRegistry;
import net.poob22.normaldm.common.server.mobeffects.NDMEffects;

import static net.poob22.normaldm.NormalDungeonMod.MODID;
import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonBusEvents {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {}

    @SubscribeEvent
    public static void onLivingKnockBackEvent(LivingKnockBackEvent event) {
        DamageSource source = event.getEntity().getLastDamageSource();

        if(source != null && source.is(NDMTagRegistry.BEAM_DAMAGE)) {
            event.setStrength(0.1F);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();

        if(player.level().dimension().location().getNamespace().equals(MODID)) {
            ItemStack stack = event.getItem().getItem();
            if(stack.getItem() instanceof StatItem statItem) {
                statItem.applyStats(player);

                player.getCapability(COMBAT).ifPresent(c -> {
                    c.getInventory().addItem(statItem);
                });

                stack.shrink(1);
                NotificationManager.addItemPickupNotification(new ItemPickupNotification(statItem.getTitle(), statItem.getSubtitle(), 80, 2));
                // allows for achievements to be processed
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinLevel(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.getCapability(COMBAT).ifPresent(c -> {
            c.getStats().applyPlayerAttributeStats(player);
        });
    }

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

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getTarget() instanceof LivingEntity living
                && living.hasEffect(NDMEffects.ACID.get())) {

            PacketHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new EffectInstanceSyncPacket(living.getUUID(), true)
            );
        }
    }

    private static void sync(LivingEntity entity, boolean active) {
        PacketHandler.sendToTracking(entity, new EffectInstanceSyncPacket(entity.getUUID(), active));
    }
}
