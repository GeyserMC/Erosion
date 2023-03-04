package org.geysermc.erosion.bukkit;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.Bukkit;
import org.geysermc.erosion.bukkit.world.*;
import org.geysermc.geyser.adapters.spigot.SpigotAdapters;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ErosionBukkitUtils {

    public static WorldAccessor determineWorldAccessor() {
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            ProtocolVersion serverVersion = reasonablyGuessServerVersion();
            if (serverVersion.getVersion() < ProtocolVersion.v1_13.getVersion()) {
                return new PreFlatteningWorldAccessor();
            }
            List<ProtocolPathEntry> path = Via.getManager().getProtocolManager()
                    .getProtocolPath(ProtocolVersion.v1_19_3.getVersion(),
                            serverVersion.getVersion());
            if (path != null) {
                List<MappingData> data = path.stream()
                        .map(entry -> entry.protocol().getMappingData())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (!data.isEmpty()) {
                    return new ViaVersionWorldAccessor(determineBaseWorldAccessor(), data);
                }
            }
        }

        return determineBaseWorldAccessor();
    }

    private static WorldAccessor determineBaseWorldAccessor() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersion = name.substring(name.lastIndexOf('.') + 1);
        WorldAccessor worldAccessor;
        try {
            SpigotAdapters.registerWorldAdapter(nmsVersion);
            worldAccessor = new AdapterWorldAccessor(SpigotAdapters.getWorldAdapter());
        } catch (Exception e) {
            try {
                worldAccessor = new PaperReflectionWorldAccessor();
            } catch (IllegalStateException mappingsNotFound) {
                worldAccessor = new ReflectionWorldAccessor();
            }
        }
        return worldAccessor;
    }

    private static ProtocolVersion reasonablyGuessServerVersion() {
        // Turn "(MC: 1.16.4)" into 1.16.4.
        String minecraftVersion = Bukkit.getServer().getVersion().split("\\(MC: ")[1].split("\\)")[0];
        return ProtocolVersion.getClosest(minecraftVersion);
    }

    public static Method findBlockNmsMethod() {
        String craftBlockData = BukkitUtils.getCraftBukkitPackage() + ".block.CraftBlock";
        try {
            Class<?> blockDataClazz = Class.forName(craftBlockData);
            return blockDataClazz.getMethod("getNMS");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private ErosionBukkitUtils() {
    }
}
