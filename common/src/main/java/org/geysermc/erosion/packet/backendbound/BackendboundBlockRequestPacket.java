package org.geysermc.erosion.packet.backendbound;

import com.nukkitx.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

public final class BackendboundBlockRequestPacket implements BackendboundPacket {
    private final Vector3i pos;

    public BackendboundBlockRequestPacket(Vector3i pos) {
        this.pos = pos;
    }

    public BackendboundBlockRequestPacket(ByteBuf buf) {
        this.pos = ProtocolUtils.readBlockPos(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        ProtocolUtils.writeBlockPos(buf, this.pos);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBlockRequest(this);
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
