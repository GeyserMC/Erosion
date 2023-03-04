package org.geysermc.erosion.bukkit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.ErosionConfig;
import org.geysermc.erosion.packet.Packets;
import org.geysermc.erosion.packet.geyserbound.GeyserboundHandshakePacket;

import java.io.IOException;

public final class PluginMessageHandler implements Listener {
    private final PayloadInterceptor interceptor;
    private final Plugin plugin;

    public PluginMessageHandler(PayloadInterceptor interceptor, Plugin plugin) {
        this.interceptor = interceptor;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessageRegister(final PlayerRegisterChannelEvent event) {
        if (Constants.PLUGIN_MESSAGE.equals(event.getChannel())) {
            Player player = event.getPlayer();
            GeyserboundHandshakePacket packet = new GeyserboundHandshakePacket(ErosionConfig.getInstance().isUnixDomainEnabled() ?
                    ErosionConfig.getInstance().getUnixDomainAddress() : null);
            ByteBuf buf = Unpooled.buffer();
            try {
                Packets.encode(buf, packet);
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                player.sendPluginMessage(plugin, Constants.PLUGIN_MESSAGE, bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                buf.release();
            }

            if (interceptor != null) {
                BukkitPacketHandler handler = interceptor.inject(player);
                ErosionBukkit.ACTIVE_PLAYERS.put(player, handler);
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent event) {
        ErosionBukkit.ACTIVE_PLAYERS.remove(event.getPlayer());
    }
}
