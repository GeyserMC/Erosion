package org.geysermc.erosion.packet.geyserbound;

import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

public final class GeyserboundBlockDataPacket implements GeyserboundPacket {
    private final String blockData;

    public GeyserboundBlockDataPacket(String blockData) {
        this.blockData = blockData;
    }

    public GeyserboundBlockDataPacket(ByteBuf buf) {
        this.blockData = ProtocolUtils.readString(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        ProtocolUtils.writeString(buf, this.blockData);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockData(this);
    }

    public String getBlockData() {
        return blockData;
    }

    @Override
    public String toString() {
        return "GeyserboundBlockDataPacket{" +
                "blockData='" + blockData + '\'' +
                '}';
    }
}
