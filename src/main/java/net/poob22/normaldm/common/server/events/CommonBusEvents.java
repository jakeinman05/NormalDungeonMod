package net.poob22.normaldm.common.server.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.common.server.misc.NDMTagRegistry;

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
}
