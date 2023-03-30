package org.geysermc.erosion.packet.backendbound;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

public final class BackendboundBlockRequestPacket implements BackendboundPacket {
    private final int transactionId;
    private final Vector3i pos;

    public BackendboundBlockRequestPacket(int transactionId, Vector3i pos) {
        this.transactionId = transactionId;
        this.pos = pos;
    }

    public BackendboundBlockRequestPacket(ByteBuf buf) {
        this.transactionId = VarInts.readUnsignedInt(buf);
        this.pos = ProtocolUtils.readBlockPos(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.transactionId);
        ProtocolUtils.writeBlockPos(buf, this.pos);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBlockRequest(this);
    }

    public int getTransactionId() {
        return transactionId;
    }

    public Vector3i getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return "BackendboundBlockRequestPacket{" +
                "pos=" + pos +
                '}';
    }
}
