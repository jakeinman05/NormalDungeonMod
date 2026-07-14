package net.poob22.normaldm.common.server.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.coremod.transformer.CoreModBaseTransformer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.render.gui.ItemTitleRenderer;
import net.poob22.normaldm.common.client.render.gui.NotificationManager;
import net.poob22.normaldm.common.server.items.stat_modifiers.StatItem;
import net.poob22.normaldm.common.server.misc.NDMTagRegistry;

import static net.poob22.normaldm.NormalDungeonMod.MODID;
import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

@Mod.EventBusSubscriber
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
                stack.shrink(1);
                NotificationManager.addItemTitleRenderer(new ItemTitleRenderer(statItem.getTitle(), statItem.getSubtitle(), 80, 2));
                // allows for achievements to be processed
                event.setResult(Event.Result.ALLOW);
            }

        } else {
            NormalDungeonMod.LOGGER.info("Event: onItemPickup:: checkLevel failed");
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinLevel(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.getCapability(COMBAT).ifPresent(c -> {
            c.getStats().applyPlayerAttributeStats(player);
        });
    }
}
