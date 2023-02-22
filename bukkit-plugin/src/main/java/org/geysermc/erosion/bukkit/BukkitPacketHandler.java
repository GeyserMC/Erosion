package org.geysermc.erosion.bukkit;

import com.nukkitx.math.vector.Vector3i;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.geysermc.erosion.packet.backendbound.BackendboundBatchBlockRequestPacket;
import org.geysermc.erosion.packet.backendbound.BackendboundBlockRequestPacket;
import org.geysermc.erosion.packet.backendbound.BackendboundInitializePacket;
import org.geysermc.erosion.packet.backendbound.BackendboundPacketHandler;
import org.geysermc.erosion.packet.geyserbound.GeyserboundBlockDataPacket;
import org.geysermc.erosion.packet.geyserbound.GeyserboundPacket;

import java.util.logging.Logger;

public final class BukkitPacketHandler implements BackendboundPacketHandler {
    private final Logger logger;
    private Channel channel;
    private Player player;

    public BukkitPacketHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handleInitialization(BackendboundInitializePacket packet) {
        player = Bukkit.getPlayer(packet.getUuid());
        if (player == null) {
            this.logger.warning("Player with UUID " + packet.getUuid() + " not found.");
        }
        ErosionBukkit.ACTIVE_PLAYERS.put(player, this);
    }

    @Override
    public void handleBatchBlockRequest(BackendboundBatchBlockRequestPacket packet) {

    }

    @Override
    public void handleBlockRequest(BackendboundBlockRequestPacket packet) {
        Vector3i pos = packet.getPos();
        Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        sendPacket(new GeyserboundBlockDataPacket(packet.getId(), block.getBlockData().getAsString()));
    }

    @Override
    public void onDisconnect() {
        ErosionBukkit.ACTIVE_PLAYERS.remove(player);
    }

    public void sendPacket(GeyserboundPacket packet) {
        this.channel.writeAndFlush(packet);
    }

    @Override
    public BukkitPacketHandler setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }
}
