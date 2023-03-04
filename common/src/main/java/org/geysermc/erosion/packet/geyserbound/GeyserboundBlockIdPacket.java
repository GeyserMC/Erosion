package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;

public final class GeyserboundBlockIdPacket implements GeyserboundPacket {
    private final int blockId;

    public GeyserboundBlockIdPacket(int blockId) {
        this.blockId = blockId;
    }

    public GeyserboundBlockIdPacket(ByteBuf buf) {
        this.blockId = VarInts.readUnsignedInt(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.blockId);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockId(this);
    }

    public int getBlockId() {
        return blockId;
    }

    @Override
    public String toString() {
        return "GeyserboundBlockIdPacket{" +
                "blockId=" + blockId +
                '}';
    }
}
