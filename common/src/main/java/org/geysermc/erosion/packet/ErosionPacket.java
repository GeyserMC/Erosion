package org.geysermc.erosion.packet;

import io.netty.buffer.ByteBuf;

public interface ErosionPacket<T extends ErosionPacketHandler> {
    void serialize(ByteBuf buf);

    void handle(T packetHandler);
}
