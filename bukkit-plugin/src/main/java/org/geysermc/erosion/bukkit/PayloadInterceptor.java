package org.geysermc.erosion.bukkit;

import org.bukkit.entity.Player;

public interface PayloadInterceptor {
    BukkitPacketHandler inject(Player player);
}
