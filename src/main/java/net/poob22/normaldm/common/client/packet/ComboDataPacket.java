package net.poob22.normaldm.common.client.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT_DATA_CAPABILITY;

public class ComboDataPacket {
    int combos;

    public ComboDataPacket(int combos) {
        this.combos = combos;
    }

    public static void encode(ComboDataPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.combos);
    }

    public static ComboDataPacket decode(FriendlyByteBuf buf) {
        return new ComboDataPacket(buf.readInt());
    }

    public static void handle(ComboDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;

            if(player == null) throw new NullPointerException("ServerPlayer on normaldm:ComboDataPacket is null");

            player.getCapability(COMBAT_DATA_CAPABILITY).ifPresent(c -> {
                c.syncCombosData(msg.combos);
            });
        });

        ctx.get().setPacketHandled(true);
    }
}
