package org.geysermc.erosion.bukkit;

import com.nukkitx.math.vector.Vector3i;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class BukkitUtils {

    public static Vector3i getVector(Location location) {
        return Vector3i.from(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static String getCraftBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName();
    }

    public static String getLegacyNmsPackage() {
        return getCraftBukkitPackage().replace("org.bukkit.craftbukkit", "net.minecraft.server");
    }

    private BukkitUtils() {
    }
}
