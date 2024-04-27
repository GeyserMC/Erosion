package org.geysermc.erosion.bukkit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.bukkit.entity.Player;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.packet.ErosionPacket;
import org.geysermc.erosion.packet.Packets;
import org.geysermc.erosion.packet.backendbound.BackendboundPacket;
import org.geysermc.erosion.packet.backendbound.BackendboundPacketHandler;
import org.geysermc.erosion.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;

/**
 * We want to avoid processing all packets on the server thread.
 */
@ChannelHandler.Sharable
public final class CustomPayloadInterceptor extends ChannelInboundHandlerAdapter implements PayloadInterceptor {
    private static final Class<?> customPayloadClass = findCustomPayloadClass();
    private static final Field channelField = findGetChannel();
    private static final Field bufField = findByteBuf();

    private final Function<Player, BukkitPacketHandler> createHandler;

    public CustomPayloadInterceptor(Function<Player, BukkitPacketHandler> createHandler) {
        this.createHandler = createHandler;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg.getClass() == customPayloadClass) {
            String channel = channelField.get(msg).toString();
            if (Constants.PLUGIN_MESSAGE.equals(channel)) {
                ByteBuf buf = (ByteBuf) bufField.get(msg);
                buf.markReaderIndex();
                if (buf.isReadable()) {
                    ErosionPacket<?> packet = Packets.decode(buf);
                    BackendboundPacketHandler handler = ctx.channel().attr(HANDLER_KEY).get();
                    ((BackendboundPacket) packet).handle(handler);
                }
                buf.resetReaderIndex();
            }
        }
        // Always forward this
        super.channelRead(ctx, msg);
    }

    private static Class<?> findCustomPayloadClass() {
        try {
            return Class.forName("net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket");
        } catch (ClassNotFoundException e) {
            try {
                // We're using pre-1.20.2
                return Class.forName("net.minecraft.network.protocol.game.PacketPlayInCustomPayload");
            } catch (ClassNotFoundException e2) {
                try {
                    // We're using pre-1.17
                    String prefix = BukkitUtils.getLegacyNmsPackage();
                    return Class.forName(prefix + ".PacketPlayInCustomPayload");
                } catch (ClassNotFoundException e3) {
                    RuntimeException runtimeException = new RuntimeException(e3);
                    runtimeException.addSuppressed(e2);
                    runtimeException.addSuppressed(e);
                    throw runtimeException;
                }
            }
        }
    }

    private static Field findGetChannel() {
        Field channel = Arrays.stream(customPayloadClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> {
                    Class<?> clazz = field.getType();
                    return clazz == String.class || "MinecraftKey".equals(clazz.getSimpleName());
                })
                .findAny()
                .orElseThrow(RuntimeException::new);
        if (!channel.isAccessible()) {
            channel.setAccessible(true);
        }
        return channel;
    }

    private static Field findByteBuf() {
        Field buf = Arrays.stream(customPayloadClass.getDeclaredFields())
                .filter(field -> ByteBuf.class.isAssignableFrom(field.getType()))
                .findAny()
                .orElseThrow(RuntimeException::new);
        if (!buf.isAccessible()) {
            buf.setAccessible(true);
        }
        return buf;
    }

    private static final Method getPlayerHandle;
    private static final Field connection;
    private static final Field networkManager;
    private static final Field playerChannelField;

    static {
        ReflectionUtils.prefix = BukkitUtils.getLegacyNmsPackage();
        try {
            getPlayerHandle = Class.forName(BukkitUtils.getCraftBukkitPackage() + ".entity.CraftPlayer")
                    .getMethod("getHandle");
            Class<?> playerConnectionClass = ReflectionUtils.getClassOrFallback(
                    "net.minecraft.server.network.PlayerConnection", ReflectionUtils.prefix + ".PlayerConnection");
            connection = ReflectionUtils.getFieldOfType(getPlayerHandle.getReturnType(), playerConnectionClass);
            Class<?> networkManagerClass = ReflectionUtils.getClassOrFallback(
                    "net.minecraft.network.NetworkManager", ReflectionUtils.prefix + ".NetworkManager");
            networkManager = ReflectionUtils.getFieldOfType(connection.getType(), networkManagerClass);
            playerChannelField = ReflectionUtils.getFieldOfType(networkManagerClass, Channel.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static final AttributeKey<BukkitPacketHandler> HANDLER_KEY = AttributeKey.valueOf("erosion-handler");

    @Override
    public BukkitPacketHandler inject(Player player) {
        try {
            Object serverPlayer = getPlayerHandle.invoke(player);
            Object playerConnection = connection.get(serverPlayer);
            Object networkManagerObject = networkManager.get(playerConnection);
            Channel channel = (Channel) playerChannelField.get(networkManagerObject);

            channel.pipeline().addBefore("packet_handler", "erosion_payload_interceptor", this);
            BukkitPacketHandler handler = createHandler.apply(player);
            channel.attr(HANDLER_KEY).set(handler);
            return handler;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
