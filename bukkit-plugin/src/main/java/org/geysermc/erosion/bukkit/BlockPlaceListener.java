package org.geysermc.erosion.bukkit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.geysermc.erosion.bukkit.BukkitUtils;
import org.geysermc.erosion.bukkit.world.WorldAccessor;
import org.geysermc.erosion.packet.geyserbound.GeyserboundBlockPlacePacket;

public final class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        BukkitPacketHandler handler = ErosionBukkit.ACTIVE_PLAYERS.get(player);
        if (handler == null) {
            return;
        }
        Location location = event.getBlockPlaced().getLocation();
        int networkId = handler.getWorldAccessor().getBlockAt(player, location);
        handler.sendPacket(new GeyserboundBlockPlacePacket(BukkitUtils.getVector(location), networkId));
    }
}
