package net.poob22.normaldm.common.client.packet.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.poob22.normaldm.NormalDungeonMod;

import java.util.function.Supplier;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class StatsDataPacket implements ComponentDataPacket {
    float damage;
    float reach;
    float health;
    float atkSpeed;
    float moveSpeed;
    float cooldownMult;

    public StatsDataPacket(float damage, float reach, float health, float atkSpeed, float moveSpeed, float cooldownMult) {
        this.damage = damage;
        this.reach = reach;
        this.health = health;
        this.atkSpeed = atkSpeed;
        this.moveSpeed = moveSpeed;
        this.cooldownMult = cooldownMult;
    }

    public static void encode(StatsDataPacket packet, FriendlyByteBuf buf) {
        buf.writeFloat(packet.damage);
        buf.writeFloat(packet.reach);
        buf.writeFloat(packet.health);
        buf.writeFloat(packet.atkSpeed);
        buf.writeFloat(packet.moveSpeed);
        buf.writeFloat(packet.cooldownMult);
    }

    public static StatsDataPacket decode(FriendlyByteBuf buf) {
        return new StatsDataPacket(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(StatsDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if(player == null) {
                NormalDungeonMod.LOGGER.error("normaldm:StatsDataPacket::handle: player is null, packet ignored.");
                return;
            }

            player.getCapability(COMBAT).ifPresent(c -> {
                c.getStats().sync(msg.damage, msg.reach, msg.health, msg.atkSpeed, msg.moveSpeed, msg.cooldownMult);
            });
        });

        ctx.get().setPacketHandled(true);
    }
}
