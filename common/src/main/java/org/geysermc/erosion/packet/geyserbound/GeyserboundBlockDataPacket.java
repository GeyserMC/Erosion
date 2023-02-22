package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.IdBased;
import org.geysermc.erosion.packet.ProtocolUtils;

public final class GeyserboundBlockDataPacket implements GeyserboundPacket, IdBased {
    private final int id;
    private final String blockData;

    public GeyserboundBlockDataPacket(int id, String blockData) {
        this.id = id;
        this.blockData = blockData;
    }

    public GeyserboundBlockDataPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
        this.blockData = ProtocolUtils.readString(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
        ProtocolUtils.writeString(buf, this.blockData);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockData(this);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getBlockData() {
        return blockData;
    }

    @Override
    public String toString() {
        return "GeyserboundBlockDataPacket{" +
                "id=" + id +
                ", blockData='" + blockData + '\'' +
                '}';
    }
}
