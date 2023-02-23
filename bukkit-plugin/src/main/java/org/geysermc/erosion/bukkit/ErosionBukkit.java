package org.geysermc.erosion.bukkit;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.bukkit.world.WorldAccessor;
import org.geysermc.erosion.netty.impl.UnixSocketListener;
import org.geysermc.erosion.packet.Packets;

import java.util.Map;

public final class ErosionBukkit extends JavaPlugin {
    public static final Map<Player, BukkitPacketHandler> ACTIVE_PLAYERS
            = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    private UnixSocketListener listener;

    @Override
    public void onEnable() {
        WorldAccessor worldAccessor = BukkitUtils.determineWorldAccessor();
        Packets.initBackend();

        listener = new UnixSocketListener();
        listener.createServer("/tmp/geyser.sock", new BukkitPacketHandler(getLogger(), worldAccessor));

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.PLUGIN_MESSAGE);
        Bukkit.getPluginManager().registerEvents(new PluginMessageHandler(this), this);

        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(worldAccessor), this);
    }

    @Override
    public void onDisable() {
        ACTIVE_PLAYERS.clear();

        try {
            listener.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
