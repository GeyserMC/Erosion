package org.geysermc.erosion.packet.backendbound;

import org.geysermc.erosion.packet.ErosionPacketHandler;

public interface BackendboundPacketHandler extends ErosionPacketHandler {
    void handleInitialization(BackendboundInitializePacket packet);

    void handleBatchBlockRequest(BackendboundBatchBlockRequestPacket packet);

    void handleBatchBlockEntity(BackendboundBatchBlockEntityPacket packet);

    void handleBlockRequest(BackendboundBlockRequestPacket packet);

    void handleBlockEntity(BackendboundBlockEntityPacket packet);

    void handlePickBlock(BackendboundPickBlockPacket packet);
}
