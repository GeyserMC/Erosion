package org.geysermc.erosion.netty.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import org.geysermc.erosion.netty.ErosionConnection;
import org.geysermc.erosion.netty.ErosionPacketDecoder;
import org.geysermc.erosion.netty.ErosionPacketEncoder;
import org.geysermc.erosion.netty.VarIntLengthManager;
import org.geysermc.erosion.packet.ErosionPacketHandler;
import org.geysermc.erosion.packet.backendbound.BackendboundPacketHandler;
import org.geysermc.erosion.packet.geyserbound.GeyserboundPacketHandler;

import java.util.function.Supplier;

// TODO split into two classes
public final class UnixSocketListener {
    private Channel channel;
    private EventLoopGroup eventLoopGroup;

    public void initializeEventLoopGroup() {
        this.eventLoopGroup = new EpollEventLoopGroup();
    }

    public Channel createClient(GeyserboundPacketHandler handler) {
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

    public void close() throws InterruptedException {
        channel.close().sync();
    }

    private void initPipeline(Channel channel, ErosionPacketHandler handler) {
        channel.pipeline()
                .addLast(new VarIntLengthManager())
                .addLast(new ErosionPacketDecoder())
                .addLast(new ErosionPacketEncoder())
                .addLast(new ErosionConnection(handler.setChannel(channel)));
    }
}
