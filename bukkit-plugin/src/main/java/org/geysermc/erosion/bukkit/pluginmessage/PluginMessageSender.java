package org.geysermc.erosion.bukkit.pluginmessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.packet.ErosionPacketSender;
import org.geysermc.erosion.packet.Packets;
import org.geysermc.erosion.packet.geyserbound.GeyserboundPacket;

import java.io.IOException;

public final class PluginMessageSender implements ErosionPacketSender<GeyserboundPacket> {
    private final Plugin plugin;
    private final Player player;

    public PluginMessageSender(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void sendPacket(GeyserboundPacket packet) {
        ByteBuf buf = Unpooled.buffer();
        try {
            // TODO make common code?
            Packets.encode(buf, packet);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            player.sendPluginMessage(plugin, Constants.PLUGIN_MESSAGE, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            buf.release();
        }
    }

    @Override
    public void setChannel(Channel channel) {
    }
}
