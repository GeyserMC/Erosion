package org.geysermc.erosion.packet;

import io.netty.channel.Channel;

public interface ErosionPacketSender<T extends ErosionPacket<?>> {
    void sendPacket(T packet);

    default void sendPacketWithoutFlush(T packet) {
        sendPacket(packet);
    }

    default void flush() {
    }

    void setChannel(Channel channel);

    default void close() {
    }
}
