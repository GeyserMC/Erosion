package org.geysermc.erosion.bukkit;

import com.nukkitx.math.vector.Vector3i;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.geysermc.erosion.bukkit.world.AdapterWorldAccessor;
import org.geysermc.erosion.bukkit.world.PaperReflectionWorldAccessor;
import org.geysermc.erosion.bukkit.world.ViaVersionWorldAccessor;
import org.geysermc.erosion.bukkit.world.WorldAccessor;
import org.geysermc.geyser.adapters.spigot.SpigotAdapters;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BukkitUtils {

    public static Vector3i getVector(Location location) {
        return Vector3i.from(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static WorldAccessor determineWorldAccessor() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersion = name.substring(name.lastIndexOf('.') + 1);
        WorldAccessor worldAccessor;
        try {
            SpigotAdapters.registerWorldAdapter(nmsVersion);
            worldAccessor = new AdapterWorldAccessor(SpigotAdapters.getWorldAdapter());
        } catch (Exception e) {
            worldAccessor = new PaperReflectionWorldAccessor();
        }
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            ProtocolVersion serverVersion = reasonablyGuessServerVersion();
            List<MappingData> data = Via.getManager().getProtocolManager()
                    .getProtocolPath(ProtocolVersion.v1_19_3.getVersion(),
                            serverVersion.getVersion())
                    .stream()
                    .map(entry -> entry.protocol().getMappingData())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!data.isEmpty()) {
                return new ViaVersionWorldAccessor(worldAccessor, data);
            }
        }
        return worldAccessor;
    }

    private static ProtocolVersion reasonablyGuessServerVersion() {
        // Turn "(MC: 1.16.4)" into 1.16.4.
        String minecraftVersion = Bukkit.getServer().getVersion().split("\\(MC: ")[1].split("\\)")[0];
        return ProtocolVersion.getClosest(minecraftVersion);
    }

    private BukkitUtils() {
    }
}
