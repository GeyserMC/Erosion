package org.geysermc.erosion.bukkit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

/**
 * We want to avoid processing all packets on the server thread.
 */
public final class CustomPayloadInterceptor extends ChannelInboundHandlerAdapter {
    private static final Class<?> customPayloadClass = findCustomPayloadClass();
    //private static final MethodHandle getChannel;

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg.getClass() == customPayloadClass) {
            System.out.println(msg.toString());
        }
        // Always forward this
        super.channelRead(ctx, msg);
    }

    private static Class<?> findCustomPayloadClass() {
        try {
            return Class.forName("net.minecraft.network.protocol.game.PacketPlayInCustomPayload");
        } catch (ClassNotFoundException e) {
            try {
                // We're using pre-1.17
                String prefix = BukkitUtils.getLegacyNmsPackage();
                return Class.forName(prefix + ".PacketPlayInCustomPayload");
            } catch (ClassNotFoundException e2) {
                RuntimeException runtimeException = new RuntimeException(e2);
                runtimeException.addSuppressed(e);
                throw runtimeException;
            }
        }
    }

    private static MethodHandle findGetChannel() {
        return null;
    }
}
