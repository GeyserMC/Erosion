package org.geysermc.erosion.bukkit.world;

import org.bukkit.World;
import org.geysermc.geyser.adapters.spigot.SpigotWorldAdapter;

public final class AdapterWorldAccessor implements WorldAccessor {
    private final SpigotWorldAdapter adapter;

    public AdapterWorldAccessor(SpigotWorldAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getBlockAt(World world, int x, int y, int z) {
        return adapter.getBlockAt(world, x, y, z);
    }
}
