package org.geysermc.erosion.netty.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import org.geysermc.erosion.packet.backendbound.BackendboundPacketHandler;

import java.util.function.Supplier;

public final class UnixSocketServerListener extends AbstractUnixSocketListener {
    private Channel channel;

    public void createServer(String address, Supplier<BackendboundPacketHandler> handler) {
        channel = (new ServerBootstrap()
                .channel(EpollServerDomainSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        initPipeline(ch, handler.get());
                    }
                })
                .localAddress(new DomainSocketAddress(address)))
                .group(new EpollEventLoopGroup())
                .bind()
                .syncUninterruptibly()
                .channel();
    }

    @Override
    public void close() {
        channel.close().syncUninterruptibly();
    }
}
