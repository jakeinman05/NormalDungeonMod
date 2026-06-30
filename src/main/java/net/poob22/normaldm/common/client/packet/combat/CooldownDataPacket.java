package net.poob22.normaldm.common.client.packet.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.NormalDungeonMod;

import java.util.function.Supplier;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class CooldownDataPacket implements ComponentDataPacket {
    int cooldown;

    public CooldownDataPacket(int cooldown) {
        this.cooldown = cooldown;
    }

    public static void encode(CooldownDataPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.cooldown);
    }

    public static CooldownDataPacket decode(FriendlyByteBuf buf) {
        return new CooldownDataPacket(buf.readInt());
    }

    public static void handle(CooldownDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;

            if(player == null) {
                NormalDungeonMod.LOGGER.error("normaldm:CooldownDataPacket::handle: player is null, packet ignored.");
                return;
            }

            player.getCapability(COMBAT).ifPresent(c -> {
                c.getCooldownData().sync(msg.cooldown);
            });
        });

        ctx.get().setPacketHandled(true);
    }
}
