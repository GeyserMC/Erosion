package org.geysermc.erosion.netty;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.geysermc.erosion.packet.ErosionPacket;
import org.geysermc.erosion.packet.Packets;

import java.io.IOException;

public final class ErosionPacketEncoder extends MessageToByteEncoder<ErosionPacket<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ErosionPacket<?> msg, ByteBuf out) {
        try {
            int id = Packets.SENDING.getOrDefault(msg.getClass(), -1);
            if (id == -1) {
                throw new IOException("Unregistered packet class! " + msg);
            }
            VarInts.writeUnsignedInt(out, id);
            msg.serialize(out);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
