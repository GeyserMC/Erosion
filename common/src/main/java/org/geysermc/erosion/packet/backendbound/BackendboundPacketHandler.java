package org.geysermc.erosion.packet.backendbound;

import org.geysermc.erosion.packet.ErosionPacketHandler;

public interface BackendboundPacketHandler extends ErosionPacketHandler {
    void handleInitialization(BackendboundInitializePacket packet);

    void handleBatchBlockRequest(BackendboundBatchBlockRequestPacket packet);

    void handleBatchLecternRequest(BackendboundBatchLecternRequestPacket packet);

    void handleBlockRequest(BackendboundBlockRequestPacket packet);

    void handleLecternRequest(BackendboundLecternRequestPacket packet);

    void handlePickBlock(BackendboundPickBlockPacket packet);
}
