package org.geysermc.erosion.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.geysermc.erosion.packet.ErosionPacket;
import org.geysermc.erosion.packet.ErosionPacketHandler;

public final class ErosionConnection extends SimpleChannelInboundHandler<ErosionPacket<?>> {
    private final ErosionPacketHandler handler;

    public ErosionConnection(ErosionPacketHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ErosionPacket<?> msg) {
        genericsFtw(msg);
    }

    // Method name copied from Mojmap haha
    private <T extends ErosionPacketHandler> void genericsFtw(ErosionPacket<T> packet) {
        packet.handle((T) this.handler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handler.onConnect();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        handler.onDisconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
