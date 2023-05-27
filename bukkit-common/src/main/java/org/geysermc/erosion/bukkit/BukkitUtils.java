package org.geysermc.erosion.bukkit;

import org.bukkit.entity.Player;
import org.cloudburstmc.math.vector.Vector3i;
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

    /**
     * @return true if the distance requested is too far.
     */
    public static boolean invalidDistance(Player player, Vector3i pos) {
        return invalidDistance(player, pos.getX(), pos.getZ());
    }

    /**
     * @return true if the distance requested is too far.
     */
    public static boolean invalidDistance(Player player, int x, int z) {
        final Location location = player.getLocation();
        final int dX = Math.abs(x - location.getBlockX()) >> 4;
        final int dZ = Math.abs(z - location.getBlockZ()) >> 4;
        final int viewDistance = Bukkit.getViewDistance();
        return (dX * dX + dZ * dZ) > viewDistance * viewDistance;
    }

    /**
     * @return true if the distance requested is too far.
     */
    public static boolean invalidChunkDistance(Player player, int x, int z) {
        final Location location = player.getLocation();
        final int dX = Math.abs((x << 4) - location.getBlockX()) >> 4;
        final int dZ = Math.abs((z << 4) - location.getBlockZ()) >> 4;
        final int viewDistance = Bukkit.getViewDistance();
        return (dX * dX + dZ * dZ) > viewDistance * viewDistance;
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
