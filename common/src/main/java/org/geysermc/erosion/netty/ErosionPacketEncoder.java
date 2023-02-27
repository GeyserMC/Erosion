package org.geysermc.erosion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.geysermc.erosion.packet.ErosionPacket;
import org.geysermc.erosion.packet.Packets;

public final class ErosionPacketEncoder extends MessageToByteEncoder<ErosionPacket<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ErosionPacket<?> msg, ByteBuf out) {
        try {
            Packets.encode(out, msg);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
