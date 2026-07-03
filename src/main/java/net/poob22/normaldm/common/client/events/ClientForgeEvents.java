package net.poob22.normaldm.common.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.packet.combat.PlayerHoldingLClickPacket;

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
}
