package org.geysermc.erosion.packet.backendbound;

import org.cloudburstmc.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

public final class BackendboundBlockEntityPacket implements BackendboundPacket {
    private final Vector3i pos;

    public BackendboundBlockEntityPacket(ByteBuf buf) {
        this.pos = ProtocolUtils.readBlockPos(buf);
    }

    public BackendboundBlockEntityPacket(Vector3i pos) {
        this.pos = pos;
    }

    @Override
    public void serialize(ByteBuf buf) {
        ProtocolUtils.writeBlockPos(buf, this.pos);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBlockEntity(this);
    }

    public Vector3i getPos() {
        return pos;
    }
}
