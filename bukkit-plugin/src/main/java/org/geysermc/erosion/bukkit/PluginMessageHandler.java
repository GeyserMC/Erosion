package org.geysermc.erosion.bukkit;

import io.netty.buffer.ByteBuf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.packet.GeyserboundHandshake;

public final class PluginMessageHandler implements Listener {
    private final Plugin plugin;

    public PluginMessageHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessageRegister(final PlayerRegisterChannelEvent event) {
        if (Constants.PLUGIN_MESSAGE.equals(event.getChannel())) {
            ByteBuf buf = GeyserboundHandshake.create();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            event.getPlayer().sendPluginMessage(plugin, Constants.PLUGIN_MESSAGE, bytes);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent event) {
        ErosionBukkit.ACTIVE_PLAYERS.remove(event.getPlayer());
    }
}
