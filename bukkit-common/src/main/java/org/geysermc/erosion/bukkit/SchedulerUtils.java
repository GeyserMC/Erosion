package org.geysermc.erosion.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public final class SchedulerUtils {
    public static final boolean FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
        }

        FOLIA = folia;
    }

    public static void runTask(Plugin plugin, Runnable task, Block block) {
        if (FOLIA) {
            Bukkit.getRegionScheduler().execute(plugin, block.getLocation(), task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    private SchedulerUtils() {
    }
}
