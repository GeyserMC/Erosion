package org.geysermc.erosion.bukkit;

import com.nukkitx.math.vector.Vector3i;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

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

    private static final boolean CAN_USE_PAPER_BLOCK_STATE;

    static {
        boolean canUsePaperBlockState = false;
        try {
            Block.class.getMethod("getState", boolean.class);
            canUsePaperBlockState = true;
        } catch (NoSuchMethodException ignored) {
        }
        CAN_USE_PAPER_BLOCK_STATE = canUsePaperBlockState;
    }

    public static BlockState getBlockState(Block block) {
        if (CAN_USE_PAPER_BLOCK_STATE) {
            return block.getState(false);
        } else {
            return block.getState();
        }
    }

    private BukkitUtils() {
    }
}
