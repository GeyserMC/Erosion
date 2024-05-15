package org.geysermc.erosion.bukkit;

import com.google.common.base.Predicates;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.Bukkit;
import org.geysermc.erosion.bukkit.world.*;
import org.geysermc.geyser.adapters.paper.PaperAdapters;
import org.geysermc.geyser.adapters.spigot.SpigotAdapters;

import java.lang.reflect.Method;
import java.util.List;

public final class ErosionBukkitUtils {
    public static final WorldAccessor BASE_ACCESSOR = determineBaseWorldAccessor();

    public static WorldAccessor determinePlayerWorldAccessor(int protocolVersion) {
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            ProtocolVersion serverVersion = reasonablyGuessServerVersion();
            if (serverVersion.getVersion() < ProtocolVersion.v1_13.getVersion()) {
                return PreFlatteningWorldAccessor.INSTANCE;
            }
            List<ProtocolPathEntry> path = Via.getManager().getProtocolManager()
                    .getProtocolPath(protocolVersion, serverVersion.getVersion());
            if (path != null) {
                List<MappingData> data = path.stream()
                        .map(entry -> entry.protocol().getMappingData())
                        .filter(Predicates.notNull())
                        .toList();
                if (!data.isEmpty()) {
                    return new ViaVersionWorldAccessor(BASE_ACCESSOR, data);
                }
            }
        }

        return BASE_ACCESSOR;
    }

    private static WorldAccessor determineBaseWorldAccessor() {
        WorldAccessor worldAccessor;
        try {
            //noinspection deprecation
            int protocolVersion = Bukkit.getUnsafe().getProtocolVersion();
            PaperAdapters.registerClosestWorldAdapter(protocolVersion);
            worldAccessor = new AdapterWorldAccessor(PaperAdapters.getWorldAdapter());
        } catch (NoSuchMethodError | Exception notPaper) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String nmsVersion = name.substring(name.lastIndexOf('.') + 1);
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
