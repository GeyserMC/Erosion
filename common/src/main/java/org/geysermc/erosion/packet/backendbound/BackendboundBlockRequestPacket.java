package org.geysermc.erosion.packet.backendbound;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.IdBased;
import org.geysermc.erosion.packet.ProtocolUtils;

import java.util.UUID;

public final class BackendboundBlockRequestPacket implements BackendboundPacket, IdBased {
    private final int id;
    private final UUID uuid;
    private final Vector3i pos;

    public BackendboundBlockRequestPacket(int id, UUID uuid, Vector3i pos) {
        this.id = id;
        this.uuid = uuid;
        this.pos = pos;
    }

    public BackendboundBlockRequestPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
        this.uuid = ProtocolUtils.readUuid(buf);
        this.pos = ProtocolUtils.readBlockPos(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
        ProtocolUtils.writeUuid(buf, this.uuid);
        ProtocolUtils.writeBlockPos(buf, this.pos);
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBlockRequest(this);
    }

    @Override
    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Vector3i getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return "BackendboundBlockRequestPacket{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", pos=" + pos +
                '}';
    }
}
