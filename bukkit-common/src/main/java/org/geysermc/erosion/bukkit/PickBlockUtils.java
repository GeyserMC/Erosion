package org.geysermc.erosion.bukkit;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.geysermc.erosion.util.ReflectionUtils;

public final class PickBlockUtils {
    private static final PickItemProvider PROVIDER;

    static {
        PickItemProvider provider;
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            provider = new ViaPickItemProvider();
        } else {
            provider = null;
            // Make sure we're in an environment with NMS mapping
            if (ReflectionUtils.getClassSilently("net.minecraft.server.level.ServerPlayer") != null) {
                try {
                    Class<?> nms = Class.forName("org.geysermc.erosion.bukkit.nms.NmsPickItemProvider");
                    provider = (PickItemProvider) nms.getConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        PROVIDER = provider;
    }

    public static Int2ObjectMap<byte[]> pickBlock(Block block) {
        if (PROVIDER == null) {
            return Int2ObjectMaps.emptyMap();
        }
        return PROVIDER.getPickItem(block);
    }

    private PickBlockUtils() {
    }
}
