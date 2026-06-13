package net.poob22.normaldm.common.client.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PlayerLeftClickEmptyPacket {
    UUID playerUUID;

    public PlayerLeftClickEmptyPacket(UUID id) {
        this.playerUUID = id;
    }

    public static void encode(PlayerLeftClickEmptyPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
    }

    public static PlayerLeftClickEmptyPacket decode(FriendlyByteBuf buf) {
        return new PlayerLeftClickEmptyPacket(buf.readUUID());
    }

    public static void handle(PlayerLeftClickEmptyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if(level != null) {
                Player player = level.getPlayerByUUID(msg.playerUUID);
            }
            else {
                throw new NullPointerException("Level in PlayerLeftClickEmptyPacket is null");
            }
        });
    }
}
