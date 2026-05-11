package net.poob22.normaldm.common.client.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.common.server.entity.projectile.BaseBioluminescentBeamEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BeamPointsToClientPacket {
    final List<Vec3> pointsToClient;
    final int beamId;
    final Vec3 beamEnd;

    public BeamPointsToClientPacket(int beamId, List<Vec3> pointsToClient, Vec3 beamEnd) {
        this.beamId = beamId;
        this.pointsToClient = pointsToClient;
        this.beamEnd = beamEnd;
    }

    public static void encode(BeamPointsToClientPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.beamId);
        buf.writeInt(msg.pointsToClient.size());
        buf.writeDouble(msg.beamEnd.x);
        buf.writeDouble(msg.beamEnd.y);
        buf.writeDouble(msg.beamEnd.z);

        for(Vec3 vec : msg.pointsToClient) {
            buf.writeDouble(vec.x);
            buf.writeDouble(vec.y);
            buf.writeDouble(vec.z);
        }
    }

    public static BeamPointsToClientPacket decode(FriendlyByteBuf buf) {
        int beamId = buf.readInt();
        int size = buf.readInt();
        Vec3 beamEnd = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

        List<Vec3> pointsToClient = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            pointsToClient.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }

        return new BeamPointsToClientPacket(beamId, pointsToClient, beamEnd);
    }

    public static void handle(BeamPointsToClientPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) return;

            Entity entity = level.getEntity(msg.beamId);
            if(entity instanceof BaseBioluminescentBeamEntity beam) {
                beam.clearPoints();
                beam.setPoints(msg.pointsToClient, msg.pointsToClient.size());
                beam.setBeamEnd(msg.beamEnd);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
