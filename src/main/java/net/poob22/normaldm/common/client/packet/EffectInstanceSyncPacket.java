package net.poob22.normaldm.common.client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.common.client.render.state.ClientEffectState;

import java.util.UUID;
import java.util.function.Supplier;

public record EffectInstanceSyncPacket(UUID uuid, boolean active) {

    public static void encode(EffectInstanceSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.uuid);
        buf.writeBoolean(packet.active);
    }

    public static EffectInstanceSyncPacket decode(FriendlyByteBuf buf) {
        return new EffectInstanceSyncPacket(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(EffectInstanceSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientEffectState.set(msg.uuid, msg.active);
        });

        ctx.get().setPacketHandled(true);
    }
}
