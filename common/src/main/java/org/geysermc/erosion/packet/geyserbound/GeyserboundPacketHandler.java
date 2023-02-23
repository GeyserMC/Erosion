package org.geysermc.erosion.packet.geyserbound;

import org.geysermc.erosion.packet.ErosionPacketHandler;

public interface GeyserboundPacketHandler extends ErosionPacketHandler {
    void handleBatchBlockId(GeyserboundBatchBlockIdPacket packet);

    void handleBlockData(GeyserboundBlockDataPacket packet);

    void handleBlockId(GeyserboundBlockIdPacket packet);

    void handleBlockLookupFail(GeyserboundBlockLookupFailPacket packet);

    void handleBlockPlace(GeyserboundBlockPlacePacket packet);
}
