package net.poob22.normaldm.common.client.packet.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.NormalDungeonMod;

import java.util.function.Supplier;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class ComboDataPacket implements ComponentDataPacket {
    int combos;
    float timer;

    public ComboDataPacket(int combos, float timer) {
        this.combos = combos;
        this.timer = timer;
    }

    public static void encode(ComboDataPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.combos);
        buf.writeFloat(packet.timer);
    }

    public static ComboDataPacket decode(FriendlyByteBuf buf) {
        return new ComboDataPacket(buf.readInt(), buf.readFloat());
    }

    public static void handle(ComboDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;

            if(player == null) {
                NormalDungeonMod.LOGGER.error("normaldm:ComboDataPacket::handle: player is null, packet ignored.");
                ctx.get().setPacketHandled(false);
                return;
            }

            player.getCapability(COMBAT).ifPresent(c -> {
                c.getCombosData().sync(msg.combos, msg.timer);
            });
        });

        ctx.get().setPacketHandled(true);
    }
}
