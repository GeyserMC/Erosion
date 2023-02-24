package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.IdBased;

public final class GeyserboundBatchBlockIdPacket implements GeyserboundPacket, IdBased {
    private final int id;
    private final int[] blocks;

    public GeyserboundBatchBlockIdPacket(int id, int[] blocks) {
        this.id = id;
        this.blocks = blocks;
    }

    public GeyserboundBatchBlockIdPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
        this.blocks = new int[VarInts.readUnsignedInt(buf)];
        for (int i = 0; i < blocks.length; i++) {
            this.blocks[i] = VarInts.readUnsignedInt(buf);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
        VarInts.writeUnsignedInt(buf, this.blocks.length);
        for (int block : blocks) {
            VarInts.writeUnsignedInt(buf, block);
        }
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBatchBlockId(this);
    }

    @Override
    public int getId() {
        return id;
    }

    public int[] getBlocks() {
        return blocks;
    }

    @Override
    public String toString() {
        return "GeyserboundBatchBlockIdPacket{" +
                "id=" + id +
                ", blocks=" + blocks +
                '}';
    }
}
