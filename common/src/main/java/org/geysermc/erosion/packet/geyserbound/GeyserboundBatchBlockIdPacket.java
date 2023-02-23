package org.geysermc.erosion.packet.geyserbound;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import org.geysermc.erosion.packet.IdBased;
import org.geysermc.erosion.packet.ProtocolUtils;
public class GeyserboundBatchBlockIdPacket implements GeyserboundPacket, IdBased {
    private final int id;
    private final Object2IntMap<Vector3i> blocks;

    public GeyserboundBatchBlockIdPacket(int id, Object2IntMap<Vector3i> blocks) {
        this.id = id;
        this.blocks = blocks;
    }

    public GeyserboundBatchBlockIdPacket(ByteBuf buf) {
        this.id = VarInts.readUnsignedInt(buf);
        int size = VarInts.readUnsignedInt(buf);
        this.blocks = new Object2IntArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            Vector3i pos = ProtocolUtils.readBlockPos(buf);
            int blockId = VarInts.readUnsignedInt(buf);
            this.blocks.put(pos, blockId);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, this.id);
        VarInts.writeUnsignedInt(buf, this.blocks.size());
        Object2IntMaps.fastForEach(this.blocks, entry -> {
            ProtocolUtils.writeBlockPos(buf, entry.getKey());
            VarInts.writeUnsignedInt(buf, entry.getIntValue());
        });
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBatchBlockId(this);
    }

    @Override
    public int getId() {
        return id;
    }

    public Object2IntMap<Vector3i> getBlocks() {
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
