package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.IdBased;

/**
 * Always respond to Geyser to a transaction, even if it failed.
 */
public final class GeyserboundBlockLookupFailPacket implements GeyserboundPacket, IdBased {
    private final int id;

    public GeyserboundBlockLookupFailPacket(int id) {
        this.id = id;
    }

    public GeyserboundBlockLookupFailPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockLookupFail(this);
    }

    @Override
    public int getId() {
        return id;
    }
}
