package org.geysermc.erosion.netty.impl;

import io.netty.channel.Channel;
import org.geysermc.erosion.netty.ErosionConnection;
import org.geysermc.erosion.netty.ErosionPacketDecoder;
import org.geysermc.erosion.netty.ErosionPacketEncoder;
import org.geysermc.erosion.netty.VarIntLengthManager;
import org.geysermc.erosion.packet.ErosionPacketHandler;

public abstract class AbstractUnixSocketListener {

    protected final void initPipeline(Channel channel, ErosionPacketHandler handler) {
        channel.pipeline()
                .addLast(new VarIntLengthManager())
                .addLast(new ErosionPacketDecoder())
                .addLast(new ErosionPacketEncoder())
                .addLast(new ErosionConnection(handler.setChannel(channel)));
    }

    public abstract void close();
}
