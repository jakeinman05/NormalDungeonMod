package net.poob22.normaldm.common.client.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.poob22.normaldm.common.client.particles.NDMParticles;

import java.util.function.Supplier;

import static net.poob22.normaldm.common.client.packet.PacketHandler.CHANNEL;

public class BloodPoolPacket {
    double x, y, z;
    float size;

    public BloodPoolPacket(double x, double y, double z, float size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    public static void encode(BloodPoolPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeFloat(msg.size);
    }

    public static BloodPoolPacket decode(FriendlyByteBuf buf) {
        return new BloodPoolPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
    }

    public static void handle(BloodPoolPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                level.addParticle(NDMParticles.BLOOD_POOL.get(), msg.x, msg.y, msg.z, msg.size, 0 ,0);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
