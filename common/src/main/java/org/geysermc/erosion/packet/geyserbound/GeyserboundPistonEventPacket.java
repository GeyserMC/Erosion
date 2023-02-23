package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import org.geysermc.erosion.packet.ProtocolUtils;

public final class GeyserboundPistonEventPacket implements GeyserboundPacket {
    private static final byte IS_EXTEND_FLAG = 0x01;
    private static final byte IS_STICKY_FLAG = 0x02;

    private final int blockId;
    private final Vector3i pos;
    private final byte flags;
    private final Object2IntMap<Vector3i> attachedBlocks;

    public GeyserboundPistonEventPacket(int blockId, Vector3i pos, boolean isExtend, boolean isSticky, Object2IntMap<Vector3i> attachedBlocks) {
        this.blockId = blockId;
        this.pos = pos;
        byte flags = 0;
        if (isExtend) {
            flags |= IS_EXTEND_FLAG;
        }
        if (isSticky) {
            flags |= IS_STICKY_FLAG;
        }
        this.flags = flags;
        this.attachedBlocks = attachedBlocks;
    }

    public GeyserboundPistonEventPacket(ByteBuf buf) {
        this.blockId = VarInts.readUnsignedInt(buf);
        this.pos = ProtocolUtils.readBlockPos(buf);
        this.flags = buf.readByte();
        // Size of blocks cannot be greater than 12
        int size = buf.readByte();
        this.attachedBlocks = new Object2IntArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            int relX = buf.readByte();
            int relY = buf.readByte();
            int relZ = buf.readByte();
            int networkId = VarInts.readUnsignedInt(buf);
            this.attachedBlocks.put(pos.add(relX, relY, relZ), networkId);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, blockId);
        ProtocolUtils.writeBlockPos(buf, pos);
        buf.writeByte(flags);
        buf.writeByte(this.attachedBlocks.size());
        Object2IntMaps.fastForEach(this.attachedBlocks, entry -> {
            Vector3i pos = entry.getKey();
            buf.writeByte(pos.getX() - this.pos.getX());
            buf.writeByte(pos.getY() - this.pos.getY());
            buf.writeByte(pos.getZ() - this.pos.getZ());
            VarInts.writeUnsignedInt(buf, entry.getIntValue());
        });
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handlePistonEvent(this);
    }

    public int getBlockId() {
        return blockId;
    }

    public Vector3i getPos() {
        return pos;
    }

    public Object2IntMap<Vector3i> getAttachedBlocks() {
        return attachedBlocks;
    }

    public boolean isExtend() {
        return (this.flags & IS_EXTEND_FLAG) != 0;
    }

    public boolean isSticky() {
        return (this.flags & IS_STICKY_FLAG) != 0;
    }

    @Override
    public String toString() {
        return "GeyserboundPistonEventPacket{" +
                "blockId=" + blockId +
                ", pos=" + pos +
                ", flags=" + flags +
                ", attachedBlocks=" + attachedBlocks +
                '}';
    }
}
