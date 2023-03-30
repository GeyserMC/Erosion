package org.geysermc.erosion.bukkit;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
            sendPacket(new GeyserboundBlockLookupFailPacket(0));
        }
    }

    @Override
    public void handleBatchBlockEntity(BackendboundBatchBlockEntityPacket packet) {
        if (SchedulerUtils.FOLIA) {
            Chunk chunk = getChunk(packet.getX(), packet.getZ());
            if (chunk == null) {
                return;
            }
            Bukkit.getRegionScheduler().execute(this.plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), () -> {
                List<Vector3i> blockEntityInfos = packet.getBlockEntityInfos();
                sendLecterns(chunk, blockEntityInfos);
            });
        } else {
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                Chunk chunk = getChunk(packet.getX(), packet.getZ());
                if (chunk == null) {
                    return;
                }
                List<Vector3i> blockEntityInfos = packet.getBlockEntityInfos();
                sendLecterns(chunk, blockEntityInfos);
            });
        }
    }

    private Chunk getChunk(int x, int z) {
        World world = this.player.getWorld();
        if (!world.isChunkLoaded(x, z)) {
            return null;
        }
        return world.getChunkAt(x, z);
    }

    private void sendLecterns(Chunk chunk, List<Vector3i> blockEntityInfos) {
        for (int i = 0; i < blockEntityInfos.size(); i++) {
            Vector3i info = blockEntityInfos.get(i);
            Block block = chunk.getBlock(info.getX(), info.getY(), info.getZ());
            NbtMap blockEntityData = this.lecterns.getLecternData(block, true);
            if (blockEntityData != null) {
                this.packetSender.sendPacketWithoutFlush(new GeyserboundBlockEntityPacket(blockEntityData));
            }
        }
        this.packetSender.flush();
    }

    @Override
    public void handleBlockRequest(BackendboundBlockRequestPacket packet) {
        try {
            Vector3i pos = packet.getPos();
            int networkId = worldAccessor.getBlockAt(player, pos.getX(), pos.getY(), pos.getZ());
            sendPacket(new GeyserboundBlockIdPacket(packet.getTransactionId(), networkId));
        } catch (Throwable e) {
            e.printStackTrace();
            sendPacket(new GeyserboundBlockLookupFailPacket(packet.getTransactionId() + 1));
        }
    }

    @Override
    public void handleBlockEntity(BackendboundBlockEntityPacket packet) {
        final Vector3i pos = packet.getPos();
        final Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        SchedulerUtils.runTask(this.plugin, () -> {
            final NbtMap blockEntityData = this.lecterns.getLecternData(block, false);
            if (blockEntityData != null) {
                sendPacket(new GeyserboundBlockEntityPacket(blockEntityData));
            }
        }, block);
    }

    @Override
    public void handlePickBlock(BackendboundPickBlockPacket packet) {
        Vector3i pos = packet.getPos();
        Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        SchedulerUtils.runTask(this.plugin, () -> {
            CompoundTag tag = PickBlockUtils.pickBlock(block);
            sendPacket(new GeyserboundPickBlockPacket(tag));
        }, block);
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
