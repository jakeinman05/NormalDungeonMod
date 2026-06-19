package net.poob22.normaldm.common.server.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.packet.PlayerLeftClickEmptyPacket;
import net.poob22.normaldm.common.server.combat.LeftClickHandler;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber
public class CombatEvents {

    @SubscribeEvent
    public static void dungeonLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if(player != null) {
            if(checkPunch(player)) {
                PacketHandler.sendToServer(new PlayerLeftClickEmptyPacket(player.getUUID(), player.getDeltaMovement()));
            }
        }
    }

    @SubscribeEvent
    public static void dungeonLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        punchEvent(event);
    }

    @SubscribeEvent
    public static void dungeonLeftClickEntity(AttackEntityEvent event) {
        punchEvent(event);
    }

    private static void punchEvent(PlayerEvent event) {
        Player player = event.getEntity();
        if(player != null) {
            if(checkPunch(player)) {
                LeftClickHandler.catchInput(player, player.getDeltaMovement());
                event.setCanceled(true);
            }
        }
    }

    private static boolean checkPunch(Player player) {
        ItemStack stack = player.getMainHandItem();
        ResourceLocation dim = player.level().dimension().location();
        return stack.is(ItemStack.EMPTY.getItem()) && dim.getNamespace().equals(MODID);
    }
}
