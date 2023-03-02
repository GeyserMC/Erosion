package org.geysermc.erosion.packet.geyserbound;

import io.netty.buffer.ByteBuf;

/**
 * Always respond to Geyser to a transaction, even if it failed.
 */
public final class GeyserboundBlockLookupFailPacket implements GeyserboundPacket {

    public GeyserboundBlockLookupFailPacket() {
    }

    public GeyserboundBlockLookupFailPacket(ByteBuf buf) {
    }

    @Override
    public void serialize(ByteBuf buf) {
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockLookupFail(this);
    }
}
