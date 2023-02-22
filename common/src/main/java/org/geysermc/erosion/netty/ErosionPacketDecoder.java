package org.geysermc.erosion.netty;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.geysermc.erosion.packet.ErosionPacket;
import org.geysermc.erosion.packet.Packets;

import java.util.List;
import java.util.function.Function;

public final class ErosionPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            if (!in.isReadable()) {
                return;
            }

            int id = VarInts.readUnsignedInt(in);
            Function<ByteBuf, ? extends ErosionPacket<?>> constructor = Packets.RECEIVING.get(id); // TODO add bounds check
            ErosionPacket<?> packet = constructor.apply(in);
            out.add(packet);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
