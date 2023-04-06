package org.geysermc.erosion.packet.geyserbound;

import org.cloudburstmc.protocol.common.util.VarInts;
import io.netty.buffer.ByteBuf;

/**
 * Always respond to Geyser to a transaction, even if it failed.
 */
public final class GeyserboundBlockLookupFailPacket implements GeyserboundPacket {
    /**
     * 0 means batch block request packet, otherwise subtract one for block request.
     */
    private final int transactionId;

    public GeyserboundBlockLookupFailPacket(int transactionId) {
        this.transactionId = transactionId;
    }

    public GeyserboundBlockLookupFailPacket(ByteBuf buf) {
        this.transactionId = VarInts.readUnsignedInt(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.transactionId);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockLookupFail(this);
    }

    public int getTransactionId() {
        return transactionId;
    }
}
