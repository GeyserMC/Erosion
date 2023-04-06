package org.geysermc.erosion.packet.geyserbound;

import org.cloudburstmc.protocol.common.util.VarInts;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;

public final class GeyserboundBatchBlockIdPacket implements GeyserboundPacket {
    private final int[] blocks;

    public GeyserboundBatchBlockIdPacket(int[] blocks) {
        this.blocks = blocks;
    }

    public GeyserboundBatchBlockIdPacket(ByteBuf buf) {
        this.blocks = new int[VarInts.readUnsignedInt(buf)];
        for (int i = 0; i < blocks.length; i++) {
            this.blocks[i] = VarInts.readUnsignedInt(buf);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.blocks.length);
        for (int block : blocks) {
            VarInts.writeUnsignedInt(buf, block);
        }
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBatchBlockId(this);
    }

    public int[] getBlocks() {
        return blocks;
    }

    @Override
    public String toString() {
        return "GeyserboundBatchBlockIdPacket{" +
                "blocks=" + Arrays.toString(blocks) +
                '}';
    }
}
