package org.geysermc.erosion.netty.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import org.geysermc.erosion.netty.ErosionConnection;
import org.geysermc.erosion.netty.ErosionPacketDecoder;
import org.geysermc.erosion.netty.ErosionPacketEncoder;
import org.geysermc.erosion.netty.VarIntLengthManager;
import org.geysermc.erosion.packet.ErosionPacketHandler;

public final class UnixSocketListener {
    private Channel channel;

    public void createClient(ErosionPacketHandler handler) {
        channel = (new Bootstrap()
                .channel(EpollDomainSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        initPipeline(ch, handler);
                    }
                })
                .localAddress(new DomainSocketAddress("/tmp/geyser-client.sock"))
                .group(new EpollEventLoopGroup(1))
                .bind())
                .syncUninterruptibly()
                .channel();
    }

    public void createServer(String address, ErosionPacketHandler handler) {
        channel = (new ServerBootstrap()
                .channel(EpollServerDomainSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        initPipeline(ch, handler);
                    }
                })
                .localAddress(new DomainSocketAddress(address)))
                .group(new EpollEventLoopGroup(1))
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
