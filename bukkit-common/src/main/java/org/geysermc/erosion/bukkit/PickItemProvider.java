package org.geysermc.erosion.bukkit;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.bukkit.block.Block;

public interface PickItemProvider {
    Int2ObjectMap<byte[]> getPickItem(final Block block);
}
