package org.geysermc.erosion.packet;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.geysermc.erosion.packet.backendbound.BackendboundBlockRequestPacket;
import org.geysermc.erosion.packet.geyserbound.GeyserboundBlockDataPacket;
import org.geysermc.erosion.packet.geyserbound.GeyserboundBlockIdPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Packets {
    public static final Object2IntMap<Class<? extends ErosionPacket<?>>> SENDING = new Object2IntOpenHashMap<>(); // Use Reference2Int if you use this
    public static final List<Function<ByteBuf, ? extends ErosionPacket<?>>> RECEIVING = new ArrayList<>();

    public static void initBackend() {
        int id = 0;
        registerSending(GeyserboundBlockDataPacket.class, id++);
        registerSending(GeyserboundBlockIdPacket.class, id++);

        registerReceiving(BackendboundBlockRequestPacket::new);
    }

    public static void initGeyser() {
        int id = 0;
        registerSending(BackendboundBlockRequestPacket.class, id++);

        registerReceiving(GeyserboundBlockDataPacket::new);
        registerReceiving(GeyserboundBlockIdPacket::new);
    }

    private static void registerSending(Class<? extends ErosionPacket<?>> packetClass, int id) {
        SENDING.put(packetClass, id);
    }

    private static void registerReceiving(Function<ByteBuf, ? extends ErosionPacket<?>> constructor) {
        RECEIVING.add(constructor);
    }

    private Packets() {
    }
}
