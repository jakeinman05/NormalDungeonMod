package net.poob22.normaldm.common.client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.combat.LeftClickHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class PlayerLeftClickEmptyPacket {
    UUID playerUUID;
    Vec3 deltaMovement;

    public PlayerLeftClickEmptyPacket(UUID playerUUID, Vec3 deltaMovement) {
        this.playerUUID = playerUUID;
        this.deltaMovement = deltaMovement;
    }

    public static void encode(PlayerLeftClickEmptyPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeDouble(msg.deltaMovement.x);
        buf.writeDouble(msg.deltaMovement.y);
        buf.writeDouble(msg.deltaMovement.z);
    }

    public static PlayerLeftClickEmptyPacket decode(FriendlyByteBuf buf) {
        return new PlayerLeftClickEmptyPacket(buf.readUUID(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    public static void handle(PlayerLeftClickEmptyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = ctx.get().getSender().level();
            Player player = level.getPlayerByUUID(msg.playerUUID);

            if(player == null) {
                NormalDungeonMod.LOGGER.error("normaldm:PlayerLeftClickEmptyPacket::handle: player is null, packet ignored.");
                return;
            }

            LeftClickHandler.catchInput(player, msg.deltaMovement);
        });
    }
}
