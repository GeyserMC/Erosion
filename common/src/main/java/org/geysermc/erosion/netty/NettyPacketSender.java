package org.geysermc.erosion.netty;

import io.netty.channel.Channel;
import org.geysermc.erosion.packet.ErosionPacket;
import org.geysermc.erosion.packet.ErosionPacketSender;

public final class NettyPacketSender<T extends ErosionPacket<?>> implements ErosionPacketSender<T> {
    private Channel channel;

    @Override
    public void sendPacket(T packet) {
        this.channel.writeAndFlush(packet);
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void close() {
        if (this.channel != null) {
            this.channel.close();
        }
    }
}
