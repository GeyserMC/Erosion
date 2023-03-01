package org.geysermc.erosion.packet.backendbound;

import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

import java.util.UUID;

/**
 * The first packet sent on non-plugin-message connections.
 */
public final class BackendboundInitializePacket implements BackendboundPacket {
    private final UUID uuid;

    public BackendboundInitializePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public BackendboundInitializePacket(ByteBuf buf) {
        this.uuid = ProtocolUtils.readUuid(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        ProtocolUtils.writeUuid(buf, uuid);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleInitialization(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "BackendboundInitializePacket{" +
                "uuid=" + uuid +
                '}';
    }
}
