package org.geysermc.erosion.packet.backendbound;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.common.util.VarInts;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Iterator;
import java.util.List;

public final class BackendboundBatchBlockEntityPacket implements BackendboundPacket {
    private final int x;
    private final int z;
    /**
     * X and Z are relative to chunk.
     */
    private final List<Vector3i> blockEntityInfos;

    public BackendboundBatchBlockEntityPacket(ByteBuf buf) {
        this.x = VarInts.readInt(buf);
        this.z = VarInts.readInt(buf);
        this.blockEntityInfos = new ObjectArrayList<>();
        // One entry at least is guaranteed
        boolean next;
        do {
            // xz is based on Java's chunks
            byte xz = buf.readByte();
            short y = buf.readShort();
            this.blockEntityInfos.add(Vector3i.from((xz >> 4) & 15, y >> 1, xz & 15));
            next = (y & 1) != 0;
        } while (next);
    }

    public BackendboundBatchBlockEntityPacket(int x, int z, List<Vector3i> blockEntityInfos) {
        this.x = x;
        this.z = z;
        this.blockEntityInfos = blockEntityInfos;
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeInt(buf, this.x);
        VarInts.writeInt(buf, this.z);
        Iterator<Vector3i> it = blockEntityInfos.iterator();
        while (it.hasNext()) {
            Vector3i blockEntityInfo = it.next();
            buf.writeByte(((blockEntityInfo.getX() & 15) << 4) | blockEntityInfo.getZ() & 15);
            buf.writeShort((blockEntityInfo.getY() << 1) | (it.hasNext() ? 1 : 0));
        }
    }

    @Override
    public void handle(BackendboundPacketHandler packetHandler) {
        packetHandler.handleBatchBlockEntity(this);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public List<Vector3i> getBlockEntityInfos() {
        return blockEntityInfos;
    }
}
