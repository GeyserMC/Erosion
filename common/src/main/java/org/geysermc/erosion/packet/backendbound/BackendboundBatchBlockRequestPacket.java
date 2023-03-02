package org.geysermc.erosion.packet.backendbound;

import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.util.BlockPositionIterator;

public final class BackendboundBatchBlockRequestPacket implements BackendboundPacket {
    private final BlockPositionIterator iter;

    public BackendboundBatchBlockRequestPacket(BlockPositionIterator iter) {
        this.iter = iter;
    }

    public BackendboundBatchBlockRequestPacket(ByteBuf buf) {
        this.iter = new BlockPositionIterator(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        iter.serialize(buf);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBatchBlockRequest(this);
    }

    public BlockPositionIterator getIter() {
        return iter;
    }

    @Override
    public String toString() {
        return "BackendboundBatchBlockRequestPacket{" +
                "iter=" + iter +
                '}';
    }
}
