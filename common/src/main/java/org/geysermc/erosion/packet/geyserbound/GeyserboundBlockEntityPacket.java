package org.geysermc.erosion.packet.geyserbound;

import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * For lecterns, but may be used for other things in the future...
 */
public final class GeyserboundBlockEntityPacket implements GeyserboundPacket {
    /**
     * Bedrock-formatted NBT, not Java.
     */
    @NotNull
    private final NbtMap nbt;

    public GeyserboundBlockEntityPacket(ByteBuf buf) {
        try (NBTInputStream reader = NbtUtils.createNetworkReader(new ByteBufInputStream(buf))) {
            this.nbt = (NbtMap) reader.readTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GeyserboundBlockEntityPacket(@NotNull NbtMap nbt) {
        this.nbt = nbt;
    }

    @Override
    public void serialize(ByteBuf buf) {
        try (NBTOutputStream writer = NbtUtils.createNetworkWriter(new ByteBufOutputStream(buf))) {
            writer.writeTag(this.nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleBlockEntity(this);
    }

    public NbtMap getNbt() {
        return nbt;
    }
}
