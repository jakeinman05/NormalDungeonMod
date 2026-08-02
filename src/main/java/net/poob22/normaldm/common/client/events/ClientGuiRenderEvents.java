package net.poob22.normaldm.common.client.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.common.client.render.gui.GuiOverlayRenderer;
import net.poob22.normaldm.common.client.notifications.NotificationManager;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientGuiRenderEvents {
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if(event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            GuiOverlayRenderer.render(event.getGuiGraphics());
            NotificationManager.displayFirstNotification(event.getPartialTick(), event.getGuiGraphics());
        }
    }

    @SubscribeEvent
    public static void renderOnClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            NotificationManager.tick();
        }
    }
}
