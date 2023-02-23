package org.geysermc.erosion.bukkit.world;

import org.bukkit.Location;
import org.bukkit.World;

public interface WorldAccessor {
    default int getBlockAt(World world, Location location) {
        return getBlockAt(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    int getBlockAt(World world, int x, int y, int z);
}
