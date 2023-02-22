package org.geysermc.erosion.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.erosion.netty.impl.UnixSocketListener;
import org.geysermc.erosion.packet.Packets;

public final class ErosionBukkit extends JavaPlugin {
    private UnixSocketListener listener;

    @Override
    public void onEnable() {
        Packets.initBackend();

        listener = new UnixSocketListener();
        listener.createServer("/tmp/geyser.sock", new BukkitPacketHandler());
    }
}
