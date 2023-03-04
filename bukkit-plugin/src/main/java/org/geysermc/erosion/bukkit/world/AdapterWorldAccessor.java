package org.geysermc.erosion.bukkit.world;

import org.bukkit.entity.Player;
import org.geysermc.geyser.adapters.spigot.SpigotWorldAdapter;

public final class AdapterWorldAccessor implements WorldAccessor {
    private final SpigotWorldAdapter adapter;

    public AdapterWorldAccessor(SpigotWorldAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getBlockAt(Player player, int x, int y, int z) {
        return adapter.getBlockAt(player.getWorld(), x, y, z);
    }

    @Override
    public String getLoggedName() {
        return WorldAccessor.super.getLoggedName() + "{" + adapter.getClass().getSimpleName() + "}";
    }
}
