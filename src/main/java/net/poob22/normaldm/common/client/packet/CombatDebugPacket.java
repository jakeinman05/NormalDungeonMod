package net.poob22.normaldm.common.client.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.NormalDungeonMod;

import java.util.function.Supplier;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class CombatDebugPacket {
    boolean val;

    public CombatDebugPacket(boolean val) {
        this.val = val;
    }

    public static void encode(CombatDebugPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.val);
    }

    public static CombatDebugPacket decode(FriendlyByteBuf buf) {
        return new CombatDebugPacket(buf.readBoolean());
    }

    public static void handle(CombatDebugPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;

            if(player == null) {
                NormalDungeonMod.LOGGER.error("normaldm:CombatDebugPacket::handle: player is null, packet ignored.");
                return;
            }

            player.getCapability(COMBAT).ifPresent(c -> {
                c.toggleDebug(msg.val);
            });
        });

        ctx.get().setPacketHandled(true);
    }
}
