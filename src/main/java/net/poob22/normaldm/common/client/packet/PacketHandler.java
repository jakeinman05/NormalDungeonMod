package net.poob22.normaldm.common.client.packet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.poob22.normaldm.common.client.packet.combat.ComboDataPacket;
import net.poob22.normaldm.common.client.packet.combat.CooldownDataPacket;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    public static void registerPackets() {
        CHANNEL.registerMessage(
                packetId++,
                BloodPoolPacket.class,
                BloodPoolPacket::encode,
                BloodPoolPacket::decode,
                BloodPoolPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                PlayerLeftClickEmptyPacket.class,
                PlayerLeftClickEmptyPacket::encode,
                PlayerLeftClickEmptyPacket::decode,
                PlayerLeftClickEmptyPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                ComboDataPacket.class,
                ComboDataPacket::encode,
                ComboDataPacket::decode,
                ComboDataPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                CooldownDataPacket.class,
                CooldownDataPacket::encode,
                CooldownDataPacket::decode,
                CooldownDataPacket::handle
        );
    }

    public static void sendToTracking(Entity entity, Object packet) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
}
