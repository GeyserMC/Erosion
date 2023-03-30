package org.geysermc.erosion.bukkit;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.bukkit.world.WorldAccessor;
import org.geysermc.erosion.packet.ErosionPacketSender;
import org.geysermc.erosion.packet.backendbound.*;
import org.geysermc.erosion.packet.geyserbound.*;
import org.geysermc.erosion.util.BlockPositionIterator;

import java.util.List;

public final class BukkitPacketHandler implements BackendboundPacketHandler {
    private final Plugin plugin;
    private final WorldAccessor worldAccessor;
    private final BukkitLecterns lecterns;
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
        this.lecterns = new BukkitLecterns(plugin);
    }

    @Override
    public void handleInitialization(BackendboundInitializePacket packet) {
        if (player == null) {
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
    public void handleBatchBlockEntity(BackendboundBatchBlockEntityPacket packet) {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            int x = packet.getX();
            int z = packet.getZ();
            World world = this.player.getWorld();
            if (!world.isChunkLoaded(x, z)) {
                return;
            }
            Chunk chunk = world.getChunkAt(x, z);
            List<Vector3i> blockEntityInfos = packet.getBlockEntityInfos();
            for (int i = 0; i < blockEntityInfos.size(); i++) {
                Vector3i info = blockEntityInfos.get(i);
                Block block = chunk.getBlock(info.getX(), info.getY(), info.getZ());
                NbtMap blockEntityData = this.lecterns.getLecternData(block, true);
                if (blockEntityData != null) {
                    this.packetSender.sendPacketWithoutFlush(new GeyserboundBlockEntityPacket(blockEntityData));
                }
            }
            this.packetSender.flush();
        });
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
    public void handleBlockEntity(BackendboundBlockEntityPacket packet) {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            final Vector3i pos = packet.getPos();
            final Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            final NbtMap blockEntityData = this.lecterns.getLecternData(block, false);
            if (blockEntityData != null) {
                sendPacket(new GeyserboundBlockEntityPacket(blockEntityData));
            }
        });
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
