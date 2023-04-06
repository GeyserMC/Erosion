package org.geysermc.erosion.packet.backendbound;

import org.cloudburstmc.protocol.common.util.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

import java.util.UUID;

/**
 * The first packet sent on non-plugin-message connections.
 */
public final class BackendboundInitializePacket implements BackendboundPacket {
    private final UUID uuid;
    private final int javaProtocolVersion;

    public BackendboundInitializePacket(UUID uuid, int javaProtocolVersion) {
        this.uuid = uuid;
        this.javaProtocolVersion = javaProtocolVersion;
    }

    public BackendboundInitializePacket(ByteBuf buf) {
        this.uuid = ProtocolUtils.readUuid(buf);
        this.javaProtocolVersion = VarInts.readUnsignedInt(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        ProtocolUtils.writeUuid(buf, this.uuid);
        VarInts.writeUnsignedInt(buf, this.javaProtocolVersion);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleInitialization(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getJavaProtocolVersion() {
        return javaProtocolVersion;
    }

    @Override
    public String toString() {
        return "BackendboundInitializePacket{" +
                "uuid=" + uuid +
                ", javaProtocolVersion=" + javaProtocolVersion +
                '}';
    }
}
