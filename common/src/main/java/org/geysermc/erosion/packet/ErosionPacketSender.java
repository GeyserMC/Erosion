package org.geysermc.erosion.packet;

import io.netty.channel.Channel;

public interface ErosionPacketSender<T extends ErosionPacket<?>> {
    void sendPacket(T packet);

    void setChannel(Channel channel);

    default void close() {
    }
}
