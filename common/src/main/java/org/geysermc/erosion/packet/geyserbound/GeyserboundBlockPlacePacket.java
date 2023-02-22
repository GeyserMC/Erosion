package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import org.geysermc.erosion.packet.ProtocolUtils;

/**
 * Sent to tell Geyser to play the block place sound.
 * Until/unless Geyser emulates full block interactions, this will cover edge cases Geyser can't.
 */
public final class GeyserboundBlockPlacePacket implements GeyserboundPacket {
    private final Vector3i pos;
    private final int blockId;

    public GeyserboundBlockPlacePacket(Vector3i pos, int blockId) {
        this.pos = pos;
        this.blockId = blockId;
    }

    public GeyserboundBlockPlacePacket(ByteBuf buf) {
        this.pos = ProtocolUtils.readBlockPos(buf);
        this.blockId = VarInts.readUnsignedInt(buf);
    }

    @Override
    public void serialize(ByteBuf buf) {
        ProtocolUtils.writeBlockPos(buf, this.pos);
        VarInts.writeUnsignedInt(buf, this.blockId);
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockPlace(this);
    }

    public Vector3i getPos() {
        return pos;
    }

    public int getBlockId() {
        return blockId;
    }
}
