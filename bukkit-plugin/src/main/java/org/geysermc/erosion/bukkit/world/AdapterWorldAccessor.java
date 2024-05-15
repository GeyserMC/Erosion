package org.geysermc.erosion.bukkit.world;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.geysermc.geyser.adapters.WorldAdapter;

public final class AdapterWorldAccessor implements WorldAccessor {
    private final WorldAdapter<World> adapter;

    public AdapterWorldAccessor(WorldAdapter<World> adapter) {
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
