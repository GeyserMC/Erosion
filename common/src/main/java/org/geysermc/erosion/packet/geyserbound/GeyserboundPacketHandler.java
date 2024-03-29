package org.geysermc.erosion.packet.geyserbound;

import org.geysermc.erosion.packet.ErosionPacketHandler;

public interface GeyserboundPacketHandler extends ErosionPacketHandler {
    void handleHandshake(GeyserboundHandshakePacket packet);

    void handleBatchBlockId(GeyserboundBatchBlockIdPacket packet);

    void handleBlockEntity(GeyserboundBlockEntityPacket packet);

    void handleBlockId(GeyserboundBlockIdPacket packet);

    void handleBlockLookupFail(GeyserboundBlockLookupFailPacket packet);

    void handleBlockPlace(GeyserboundBlockPlacePacket packet);

    void handlePickBlock(GeyserboundPickBlockPacket packet);

    void handlePistonEvent(GeyserboundPistonEventPacket packet);
}
