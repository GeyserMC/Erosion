package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.IdBased;

public final class GeyserboundBlockIdPacket implements GeyserboundPacket, IdBased {
    private final int id;
    private final int blockId;

    public GeyserboundBlockIdPacket(int id, int blockId) {
        this.id = id;
        this.blockId = blockId;
    }

    public GeyserboundBlockIdPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
        this.blockId = VarInts.readUnsignedInt(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
        VarInts.writeUnsignedInt(buf, this.blockId);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockId(this);
    }

    @Override
    public int getId() {
        return id;
    }

    public int getBlockId() {
        return blockId;
    }
}
