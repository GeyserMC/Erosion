package org.geysermc.erosion.bukkit;

import com.nukkitx.math.vector.Vector3i;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.geysermc.erosion.bukkit.world.WorldAccessor;
import org.geysermc.erosion.packet.geyserbound.GeyserboundPistonEventPacket;

import java.util.List;
import java.util.Map;

public final class ErosionPistonListener extends PistonListener {
    private final WorldAccessor worldAccessor;

    public ErosionPistonListener(WorldAccessor worldAccessor) {
        this.worldAccessor = worldAccessor;
    }

    @Override
    protected void onPistonAction0(BlockPistonEvent event) {
        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();
        boolean isExtend = event instanceof BlockPistonExtendEvent;

        Object2IntMap<Vector3i> attachedBlocks = new Object2IntArrayMap<>();
        boolean blocksFilled = false;

        for (Map.Entry<Player, BukkitPacketHandler> entry : ErosionBukkit.ACTIVE_PLAYERS.entrySet()) {
            Player player = entry.getKey();
            if (!player.getWorld().equals(world)) {
                continue;
            }

            int dX = Math.abs(location.getBlockX() - player.getLocation().getBlockX()) >> 4;
            int dZ = Math.abs(location.getBlockZ() - player.getLocation().getBlockZ()) >> 4;
            if ((dX * dX + dZ * dZ) > player.getClientViewDistance() * player.getClientViewDistance()) {
                // Ignore pistons outside the player's render distance
                continue;
            }

            if (!blocksFilled) {
                List<Block> blocks = isExtend ? ((BlockPistonExtendEvent) event).getBlocks() : ((BlockPistonRetractEvent) event).getBlocks();
                for (Block block : blocks) {
                    Location attachedLocation = block.getLocation();
                    int blockId = worldAccessor.getBlockAt(player, attachedLocation);
                    // TODO if we include mappings in Erosion, filter out blocks that will be destroyed
                    attachedBlocks.put(BukkitUtils.getVector(attachedLocation), blockId);
                }
                blocksFilled = true;
            }
            int pistonBlockId = worldAccessor.getBlockAt(player, location);

            entry.getValue().sendPacket(new GeyserboundPistonEventPacket(pistonBlockId, BukkitUtils.getVector(location),
                    isExtend, event.isSticky(), attachedBlocks));
        }
    }
}
