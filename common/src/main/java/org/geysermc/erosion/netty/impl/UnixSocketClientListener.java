package org.geysermc.erosion.netty.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import org.geysermc.erosion.packet.geyserbound.GeyserboundPacketHandler;

public final class UnixSocketClientListener extends AbstractUnixSocketListener {
    private EventLoopGroup eventLoopGroup;

    public void initializeEventLoopGroup() {
        if (this.eventLoopGroup == null) {
            this.eventLoopGroup = new EpollEventLoopGroup();
        }
    }

    public Channel createClient(GeyserboundPacketHandler handler) {
        initializeEventLoopGroup();
        return (new Bootstrap()
                .channel(EpollDomainSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        initPipeline(ch, handler);
                    }
                })
                .localAddress(new DomainSocketAddress("/tmp/geyser-client.sock"))
                .group(this.eventLoopGroup.next())
                .bind())
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
