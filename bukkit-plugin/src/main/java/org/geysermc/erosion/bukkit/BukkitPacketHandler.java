package org.geysermc.erosion.bukkit;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.nukkitx.math.vector.Vector3i;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.bukkit.world.WorldAccessor;
import org.geysermc.erosion.packet.ErosionPacketSender;
import org.geysermc.erosion.packet.backendbound.*;
import org.geysermc.erosion.packet.geyserbound.*;
import org.geysermc.erosion.util.BlockPositionIterator;

import java.util.logging.Logger;

public final class BukkitPacketHandler implements BackendboundPacketHandler {
    private final Plugin plugin;
    private final WorldAccessor worldAccessor;
    private final ErosionPacketSender<GeyserboundPacket> packetSender;
    private Player player;

    public BukkitPacketHandler(Plugin plugin, WorldAccessor worldAccessor, ErosionPacketSender<GeyserboundPacket> packetSender, Player player) {
        this(plugin, worldAccessor, packetSender);
        this.player = player;
    }

    public BukkitPacketHandler(Plugin plugin, WorldAccessor worldAccessor, ErosionPacketSender<GeyserboundPacket> packetSender) {
        this.plugin = plugin;
        this.worldAccessor = worldAccessor;
        this.packetSender = packetSender;
    }

    @Override
    public void handleInitialization(BackendboundInitializePacket packet) {
        if (player != null) {
            player = Bukkit.getPlayer(packet.getUuid());
            if (player == null) {
                this.plugin.getLogger().warning("Player with UUID " + packet.getUuid() + " not found.");
                return;
            }
            ErosionBukkit.ACTIVE_PLAYERS.put(player, this);
        }
    }

    @Override
    public void handleBatchBlockRequest(BackendboundBatchBlockRequestPacket packet) {
        try {
            BlockPositionIterator iter = packet.getIter();
            int[] blocks = new int[iter.getMaxIterations()];
            for (; iter.hasNext(); iter.next()) {
                int networkId = worldAccessor.getBlockAt(player, iter.getX(), iter.getY(), iter.getZ());
                blocks[iter.getIteration()] = networkId;
            }
            sendPacket(new GeyserboundBatchBlockIdPacket(blocks));
        } catch (Throwable e) {
            e.printStackTrace();
            sendPacket(new GeyserboundBlockLookupFailPacket());
        }
    }

    @Override
    public void handleBlockRequest(BackendboundBlockRequestPacket packet) {
        try {
            Vector3i pos = packet.getPos();
            int networkId = worldAccessor.getBlockAt(player, pos.getX(), pos.getY(), pos.getZ());
            sendPacket(new GeyserboundBlockIdPacket(networkId));
        } catch (Throwable e) {
            e.printStackTrace();
            sendPacket(new GeyserboundBlockLookupFailPacket());
        }
    }

    @Override
    public void handlePickBlock(BackendboundPickBlockPacket packet) {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            Vector3i pos = packet.getPos();
            CompoundTag tag = PickBlockUtils.pickBlock(this.player, pos.getX(), pos.getY(), pos.getZ());
            sendPacket(new GeyserboundPickBlockPacket(tag));
        });
    }

    @Override
    public void onDisconnect() {
        ErosionBukkit.ACTIVE_PLAYERS.remove(player);
    }

    public void sendPacket(GeyserboundPacket packet) {
        this.packetSender.sendPacket(packet);
    }

    @Override
    public BukkitPacketHandler setChannel(Channel channel) {
        this.packetSender.setChannel(channel);
        return this;
    }
}
