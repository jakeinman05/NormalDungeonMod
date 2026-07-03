package net.poob22.normaldm.common.client.packet.combat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.combat.LeftClickHandler;

import java.util.function.Supplier;

public class PlayerHoldingLClickPacket {
    Vec3 v;

    public PlayerHoldingLClickPacket(Vec3 deltaMovement) {
        this.v = deltaMovement;
    }

    public static void encode(PlayerHoldingLClickPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.v.x);
        buf.writeDouble(msg.v.y);
        buf.writeDouble(msg.v.z);
    }

    public static PlayerHoldingLClickPacket decode(FriendlyByteBuf buf) {
        return new PlayerHoldingLClickPacket(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    public static void handle(PlayerHoldingLClickPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player == null) {
                NormalDungeonMod.LOGGER.error("normaldm:PlayerHoldingLClickPacket::handle: player is null, packet ignored.");
                return;
            }
            NormalDungeonMod.LOGGER.info("Packet Received");

            LeftClickHandler.catchInput(player, msg.v);
        });

        ctx.get().setPacketHandled(true);
    }
}
