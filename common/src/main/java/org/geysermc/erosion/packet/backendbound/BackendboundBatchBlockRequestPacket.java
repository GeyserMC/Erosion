package org.geysermc.erosion.packet.backendbound;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.IdBased;
import org.geysermc.erosion.packet.ProtocolUtils;
import org.geysermc.erosion.util.BlockPositionIterator;


public final class BackendboundBatchBlockRequestPacket implements BackendboundPacket, IdBased {
    private final int id;
    private final BlockPositionIterator iter;

    public BackendboundBatchBlockRequestPacket(int id, BlockPositionIterator iter) {
        this.id = id;
        this.iter = iter;
    }

    public BackendboundBatchBlockRequestPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
        this.iter = new BlockPositionIterator(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
        iter.serialize(buf);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBatchBlockRequest(this);
    }

    @Override
    public int getId() {
        return id;
    }

    public BlockPositionIterator getIter() {
        return iter;
    }

    @Override
    public String toString() {
        return "BackendboundBatchBlockRequestPacket{" +
                "id=" + id +
                ", iter=" + iter +
                '}';
    }
}
