package org.geysermc.erosion.packet.backendbound;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.IdBased;
import org.geysermc.erosion.packet.ProtocolUtils;

import java.util.ArrayList;
import java.util.List;

public final class BackendboundBatchBlockRequestPacket implements BackendboundPacket, IdBased {
    private final int id;
    private final List<Vector3i> blocks;

    public BackendboundBatchBlockRequestPacket(int id, List<Vector3i> blocks) {
        this.id = id;
        this.blocks = blocks;
    }

    public BackendboundBatchBlockRequestPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
        int size = VarInts.readUnsignedInt(buf);
        this.blocks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.blocks.add(ProtocolUtils.readBlockPos(buf));
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
        VarInts.writeUnsignedInt(buf, this.blocks.size());
        for (int i = 0; i < this.blocks.size(); i++) {
            ProtocolUtils.writeBlockPos(buf, this.blocks.get(i));
        }
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBatchBlockRequest(this);
    }

    @Override
    public int getId() {
        return id;
    }

    public List<Vector3i> getBlocks() {
        return blocks;
    }

    @Override
    public String toString() {
        return "BackendboundBatchBlockRequestPacket{" +
                "id=" + id +
                ", blocks=" + blocks +
                '}';
    }
}
