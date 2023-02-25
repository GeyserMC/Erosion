package org.geysermc.erosion.bukkit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.packet.GeyserboundHandshake;
import org.geysermc.erosion.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PluginMessageHandler implements Listener {
    private static final Method getPlayerHandle;
    private static final Field connection;
    private static final Field networkManager;
    private static final Field channelField;

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
            channelField = ReflectionUtils.getFieldOfType(networkManagerClass, Channel.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Plugin plugin;

    public PluginMessageHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessageRegister(final PlayerRegisterChannelEvent event) {
        if (Constants.PLUGIN_MESSAGE.equals(event.getChannel())) {
            Player player = event.getPlayer();
            ByteBuf buf = GeyserboundHandshake.create();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            player.sendPluginMessage(plugin, Constants.PLUGIN_MESSAGE, bytes);

            try {
                Object serverPlayer = getPlayerHandle.invoke(player);
                Object playerConnection = connection.get(serverPlayer);
                Object networkManagerObject = networkManager.get(playerConnection);
                Channel channel = (Channel) channelField.get(networkManagerObject);

                channel.pipeline().addBefore("packet_handler", "erosion_payload_interceptor", new CustomPayloadInterceptor());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent event) {
        ErosionBukkit.ACTIVE_PLAYERS.remove(event.getPlayer());
    }
}
