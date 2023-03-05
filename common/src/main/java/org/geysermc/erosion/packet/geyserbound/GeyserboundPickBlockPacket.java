package org.geysermc.erosion.packet.geyserbound;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class GeyserboundPickBlockPacket implements GeyserboundPacket {
    private final @Nullable CompoundTag tag;

    public GeyserboundPickBlockPacket(ByteBuf buf) {
        try {
            this.tag = (CompoundTag) NBTIO.readTag((DataInput) new ByteBufInputStream(buf));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GeyserboundPickBlockPacket(@Nullable CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public void serialize(ByteBuf buf) {
        try {
            NBTIO.writeTag((DataOutput) new ByteBufOutputStream(buf), this.tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handlePickBlock(this);
    }

    @Nullable
    public CompoundTag getTag() {
        return tag;
    }
}
