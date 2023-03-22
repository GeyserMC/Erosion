package org.geysermc.erosion.packet;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.geysermc.erosion.packet.backendbound.*;
import org.geysermc.erosion.packet.geyserbound.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Packets {
    public static final Object2IntMap<Class<? extends ErosionPacket<?>>> SENDING = new Object2IntOpenHashMap<>(); // Use Reference2Int if you use this
    public static final List<Function<ByteBuf, ? extends ErosionPacket<?>>> RECEIVING = new ArrayList<>();
    private static boolean INITIALIZED = false;

    public static void initBackend() {
        if (INITIALIZED) {
            return;
        }

        int id = 0;
        registerSending(GeyserboundHandshakePacket.class, id++);
        registerSending(GeyserboundBatchBlockIdPacket.class, id++);
        registerSending(GeyserboundBlockDataPacket.class, id++);
        registerSending(GeyserboundBlockEntityPacket.class, id++);
        registerSending(GeyserboundBlockIdPacket.class, id++);
        registerSending(GeyserboundBlockLookupFailPacket.class, id++);
        registerSending(GeyserboundBlockPlacePacket.class, id++);
        registerSending(GeyserboundPickBlockPacket.class, id++);
        registerSending(GeyserboundPistonEventPacket.class, id++);

        registerReceiving(BackendboundInitializePacket::new);
        registerReceiving(BackendboundBatchBlockRequestPacket::new);
        registerReceiving(BackendboundBatchLecternRequestPacket::new);
        registerReceiving(BackendboundBlockRequestPacket::new);
        registerReceiving(BackendboundLecternRequestPacket::new);
        registerReceiving(BackendboundPickBlockPacket::new);

        INITIALIZED = true;
    }

    public static void initGeyser() {
        if (INITIALIZED) {
            return;
        }

        int id = 0;
        registerSending(BackendboundInitializePacket.class, id++);
        registerSending(BackendboundBatchBlockRequestPacket.class, id++);
        registerSending(BackendboundBatchLecternRequestPacket.class, id++);
        registerSending(BackendboundBlockRequestPacket.class, id++);
        registerSending(BackendboundLecternRequestPacket.class, id++);
        registerSending(BackendboundPickBlockPacket.class, id++);

        registerReceiving(GeyserboundHandshakePacket::new);
        registerReceiving(GeyserboundBatchBlockIdPacket::new);
        registerReceiving(GeyserboundBlockDataPacket::new);
        registerReceiving(GeyserboundBlockEntityPacket::new);
        registerReceiving(GeyserboundBlockIdPacket::new);
        registerReceiving(GeyserboundBlockLookupFailPacket::new);
        registerReceiving(GeyserboundBlockPlacePacket::new);
        registerReceiving(GeyserboundPickBlockPacket::new);
        registerReceiving(GeyserboundPistonEventPacket::new);

        INITIALIZED = true;
    }

    private static void registerSending(Class<? extends ErosionPacket<?>> packetClass, int id) {
        SENDING.put(packetClass, id);
    }

    private static void registerReceiving(Function<ByteBuf, ? extends ErosionPacket<?>> constructor) {
        RECEIVING.add(constructor);
    }

    public static ErosionPacket<?> decode(ByteBuf in) {
        int id = VarInts.readUnsignedInt(in);
        if (id >= RECEIVING.size()) {
            throw new RuntimeException("ID " + id + " does not exist");
        }
        Function<ByteBuf, ? extends ErosionPacket<?>> constructor = RECEIVING.get(id);
        ErosionPacket<?> packet = constructor.apply(in);
        if (in.isReadable()) {
            StringBuilder builder = new StringBuilder("There are leftover bytes to read after reading " + packet.getClass());
            ByteBufUtil.appendPrettyHexDump(builder, in);
            throw new RuntimeException(builder.toString());
        }
        return packet;
    }

    public static void encode(ByteBuf out, ErosionPacket<?> packet) throws IOException {
        int id = Packets.SENDING.getOrDefault(packet.getClass(), -1);
        if (id == -1) {
            throw new IOException("Unregistered packet class! " + packet);
        }
        VarInts.writeUnsignedInt(out, id);
        packet.serialize(out);
    }

    private Packets() {
    }
}
