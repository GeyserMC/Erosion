package org.geysermc.erosion.bukkit.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface WorldAccessor {
    default int getBlockAt(Player player, Location location) {
        return getBlockAt(player, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    int getBlockAt(Player player, int x, int y, int z);
}
