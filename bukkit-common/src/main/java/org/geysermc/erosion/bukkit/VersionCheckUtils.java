package org.geysermc.erosion.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

public final class VersionCheckUtils {

    /**
     * @return the server protocol, or -1 if we can't find it/the server is too old.
     */
    @SuppressWarnings("deprecation")
    public static int getServerProtocol(Logger logger, boolean debug) {
        try {
            // This method is only present on later versions of Paper
            UnsafeValues.class.getMethod("getProtocolVersion");
            return Bukkit.getUnsafe().getProtocolVersion();
        } catch (NoSuchMethodException ignored) {
        }

        // Otherwise, we can just try to find the SharedConstants class
        // It isn't present in all server versions, but if we can't find it, then we're probably not in the latest version
        Class<?> sharedConstants;
        try {
            sharedConstants = Class.forName("net.minecraft.SharedConstants");
        } catch (ClassNotFoundException e) {
            // We're using pre-1.17
            String prefix = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "net.minecraft.server");
            try {
                sharedConstants = Class.forName(prefix + ".SharedConstants");
            } catch (ClassNotFoundException e2) {
                return -1;
            }
        }
        for (Method method : sharedConstants.getMethods()) {
            if (method.getReturnType() == int.class && Modifier.isStatic(method.getModifiers())) {
                int protocolVersion;
                try {
                    protocolVersion = (int) method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.warning("Could not determine server version! This is safe to ignore, but please report to the developers: " + e.getMessage());
                    if (debug) {
                        e.printStackTrace();
                    }
                    return -1;
                }
                return protocolVersion;
            }
        }
        return -1;
    }

    private VersionCheckUtils() {
    }
}
