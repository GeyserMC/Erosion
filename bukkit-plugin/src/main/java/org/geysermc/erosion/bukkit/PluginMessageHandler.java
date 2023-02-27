package org.geysermc.erosion.bukkit;

import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.ErosionConfig;
import org.geysermc.erosion.packet.GeyserboundHandshake;

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
            ByteBuf buf = GeyserboundHandshake.create(ErosionConfig.getInstance().isUnixDomainEnabled() ?
                    ErosionConfig.getInstance().getUnixDomainAddress() : null);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            player.sendPluginMessage(plugin, Constants.PLUGIN_MESSAGE, bytes);

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
