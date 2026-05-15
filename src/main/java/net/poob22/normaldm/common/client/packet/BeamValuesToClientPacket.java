package net.poob22.normaldm.common.client.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.common.server.entity.projectile.BioluminescentBeamEntity;

import java.util.function.Supplier;

public class BeamValuesToClientPacket {
    final int beamId;
    final Vec3 shooterPos;
    final Vec3 shooterViewVec;
    final Vec3 targetPos;
    final float lerpValue;

    public BeamValuesToClientPacket(int beamId, Vec3 position, Vec3 beamDirection, Vec3 targetPos, float lerpValue) {
        this.beamId = beamId;
        this.shooterPos = position;
        this.shooterViewVec = beamDirection;
        this.targetPos = targetPos;
        this.lerpValue = lerpValue;
    }

    public static void encode(BeamValuesToClientPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.beamId);
        buf.writeDouble(msg.shooterPos.x);
        buf.writeDouble(msg.shooterPos.y);
        buf.writeDouble(msg.shooterPos.z);
        buf.writeDouble(msg.shooterViewVec.x);
        buf.writeDouble(msg.shooterViewVec.y);
        buf.writeDouble(msg.shooterViewVec.z);
        buf.writeDouble(msg.targetPos.x);
        buf.writeDouble(msg.targetPos.y);
        buf.writeDouble(msg.targetPos.z);
        buf.writeFloat(msg.lerpValue);
    }

    public static BeamValuesToClientPacket decode(FriendlyByteBuf buf) {
        int beamId = buf.readInt();
        double posX = buf.readDouble();
        double posY = buf.readDouble();
        double posZ = buf.readDouble();
        double viewX = buf.readDouble();
        double viewY = buf.readDouble();
        double viewZ = buf.readDouble();
        double targetX = buf.readDouble();
        double targetY = buf.readDouble();
        double targetZ = buf.readDouble();
        float lerpValue = buf.readFloat();

        Vec3 shooterPos = new Vec3(posX, posY, posZ);
        Vec3 shooterViewVec = new Vec3(viewX, viewY, viewZ);
        Vec3 targetPos = new Vec3(targetX, targetY, targetZ);

        return new BeamValuesToClientPacket(beamId, shooterPos, shooterViewVec, targetPos, lerpValue);
    }

    public static void handle(BeamValuesToClientPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) return;

            Entity entity = level.getEntity(msg.beamId);
            if(entity instanceof BioluminescentBeamEntity beam) {
                beam.shooterPos = msg.shooterPos;
                beam.shooterViewVec = msg.shooterViewVec;
                beam.targetPos = msg.targetPos;
                beam.lerpStrength = msg.lerpValue;

                beam.constructBeamPoints(beam.lerpStrength);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
