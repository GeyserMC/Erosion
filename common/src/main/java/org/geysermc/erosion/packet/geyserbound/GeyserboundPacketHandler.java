package org.geysermc.erosion.packet.geyserbound;

import org.geysermc.erosion.packet.ErosionPacketHandler;

public interface GeyserboundPacketHandler extends ErosionPacketHandler {
    void handleBlockData(GeyserboundBlockDataPacket packet);

    void handleBlockId(GeyserboundBlockIdPacket packet);
}
