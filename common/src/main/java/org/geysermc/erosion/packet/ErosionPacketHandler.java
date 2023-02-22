package org.geysermc.erosion.packet;

import io.netty.channel.Channel;

public interface ErosionPacketHandler {
    ErosionPacketHandler setChannel(Channel channel);

    default void onConnect() {
    }

    default void onDisconnect() {
    }
}
