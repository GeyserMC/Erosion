package org.geysermc.erosion.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.geysermc.erosion.packet.geyserbound.GeyserboundBlockPlacePacket;

public final class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        BukkitPacketHandler handler = ErosionBukkit.ACTIVE_PLAYERS.get(event.getPlayer());
        if (handler == null) {
            return;
        }
        handler.sendPacket(new GeyserboundBlockPlacePacket(BukkitUtils.getVector(event.getBlockPlaced().getLocation()), 0));
    }
}
