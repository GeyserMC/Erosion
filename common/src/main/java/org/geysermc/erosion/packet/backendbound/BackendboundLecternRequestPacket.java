package org.geysermc.erosion.packet.backendbound;

import com.nukkitx.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

public final class BackendboundLecternRequestPacket implements BackendboundPacket {
    private final Vector3i pos;

    public BackendboundLecternRequestPacket(ByteBuf buf) {
        this.pos = ProtocolUtils.readBlockPos(buf);
    }

    public BackendboundLecternRequestPacket(Vector3i pos) {
        this.pos = pos;
    }

    @Override
    public void serialize(ByteBuf buf) {
        ProtocolUtils.writeBlockPos(buf, this.pos);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleLecternRequest(this);
    }

    public Vector3i getPos() {
        return pos;
    }
}
