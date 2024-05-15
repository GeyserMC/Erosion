package org.geysermc.erosion.packet.geyserbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.cloudburstmc.protocol.common.util.VarInts;

public final class GeyserboundPickBlockPacket implements GeyserboundPacket {
    private final Int2ObjectMap<byte[]> components;

    public GeyserboundPickBlockPacket(ByteBuf buf) {
        int size = VarInts.readUnsignedInt(buf);
        this.components = new Int2ObjectOpenHashMap<>(size);
        for (int i = 0; i < size; i++) {
            int id = VarInts.readUnsignedInt(buf);
            int bufSize = VarInts.readUnsignedInt(buf);
            byte[] bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), bufSize);
            buf.readerIndex(buf.readerIndex() + bufSize);
            this.components.put(id, bytes);
        }
    }

    public GeyserboundPickBlockPacket(Int2ObjectMap<byte[]> components) {
        this.components = components;
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.components.size());
        for (Int2ObjectMap.Entry<byte[]> entry : this.components.int2ObjectEntrySet()) {
            VarInts.writeUnsignedInt(buf, entry.getIntKey());
            byte[] bytes = entry.getValue();
            VarInts.writeUnsignedInt(buf, bytes.length);
            buf.writeBytes(bytes);
        }
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handlePickBlock(this);
    }

    public Int2ObjectMap<byte[]> getComponents() {
        return components;
    }
}
