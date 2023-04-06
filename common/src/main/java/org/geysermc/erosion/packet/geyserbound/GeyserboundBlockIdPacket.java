package org.geysermc.erosion.packet.geyserbound;

import org.cloudburstmc.protocol.common.util.VarInts;
import io.netty.buffer.ByteBuf;

public final class GeyserboundBlockIdPacket implements GeyserboundPacket {
    private final int transactionId;
    private final int blockId;

    public GeyserboundBlockIdPacket(int transactionId, int blockId) {
        this.transactionId = transactionId;
        this.blockId = blockId;
    }

    public GeyserboundBlockIdPacket(ByteBuf buf) {
        this.transactionId = VarInts.readUnsignedInt(buf);
        this.blockId = VarInts.readUnsignedInt(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.transactionId);
        VarInts.writeUnsignedInt(buf, this.blockId);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockId(this);
    }

    public int getTransactionId() {
        return transactionId;
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
