package org.geysermc.erosion.packet.backendbound;

import org.geysermc.erosion.packet.ErosionPacketHandler;

public interface BackendboundPacketHandler extends ErosionPacketHandler {
    void handleBlockRequest(BackendboundBlockRequestPacket packet);
}
