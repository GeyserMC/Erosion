package org.geysermc.erosion.netty;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class VarIntLengthManager extends ByteToMessageCodec<ByteBuf> {

    private int getLengthSize(int length) {
        if ((length & -128) == 0) {
            return 1;
        } else if ((length & -16384) == 0) {
            return 2;
        } else if ((length & -2097152) == 0) {
            return 3;
        } else if ((length & -268435456) == 0) {
            return 4;
        } else {
            return 5;
        }
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        int length = in.readableBytes();
        out.ensureWritable(getLengthSize(length) + length);
        VarInts.writeUnsignedInt(out, length);
        out.writeBytes(in);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        buf.markReaderIndex();
        byte[] lengthBytes = new byte[5];
        for (int index = 0; index < lengthBytes.length; index++) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }

            lengthBytes[index] = buf.readByte();
            if (lengthBytes[index] >= 0) {
                int length = VarInts.readUnsignedInt(Unpooled.wrappedBuffer(lengthBytes));
                if (buf.readableBytes() < length) {
                    buf.resetReaderIndex();
                    return;
                }

                out.add(buf.readBytes(length));
                return;
            }
        }

        throw new CorruptedFrameException("Length is too long.");
    }
}
