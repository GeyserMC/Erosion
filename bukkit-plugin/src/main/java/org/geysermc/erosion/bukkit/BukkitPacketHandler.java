package org.geysermc.erosion.bukkit;

import com.nukkitx.math.vector.Vector3i;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.geysermc.erosion.packet.backendbound.BackendboundBlockRequestPacket;
import org.geysermc.erosion.packet.backendbound.BackendboundPacketHandler;
import org.geysermc.erosion.packet.geyserbound.GeyserboundBlockDataPacket;

public class BukkitPacketHandler implements BackendboundPacketHandler {
    private Channel channel;

    @Override
    public void handleBlockRequest(BackendboundBlockRequestPacket packet) {
        Player player = Bukkit.getPlayer(packet.getUuid());
        if (player != null) {
            Vector3i pos = packet.getPos();
            Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            System.out.println(block.getBlockData().getAsString());
            this.channel.writeAndFlush(new GeyserboundBlockDataPacket(packet.getId(), block.getBlockData().getAsString()));
        }
    }

    @Override
    public BukkitPacketHandler setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }
}
