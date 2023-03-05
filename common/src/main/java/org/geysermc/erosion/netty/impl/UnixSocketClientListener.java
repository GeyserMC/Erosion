package org.geysermc.erosion.netty.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import org.geysermc.erosion.packet.geyserbound.GeyserboundPacketHandler;

import java.net.SocketAddress;

// Could just... move this to Geyser.
public final class UnixSocketClientListener extends AbstractUnixSocketListener {
    private EventLoopGroup eventLoopGroup;

    public void initializeEventLoopGroup() {
        if (this.eventLoopGroup == null) {
            this.eventLoopGroup = new EpollEventLoopGroup();
        }
    }

    public void createClient(GeyserboundPacketHandler handler, SocketAddress address) {
        initializeEventLoopGroup();
        (new Bootstrap()
                .channel(EpollDomainSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        initPipeline(ch, handler);
                    }
                })
                .group(this.eventLoopGroup.next())
                .connect(address))
                .syncUninterruptibly()
                .channel();
    }

    @Override
    public void close() {
        if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully();
        }
    }
}
