package org.geysermc.erosion.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.ErosionConfig;
import org.geysermc.erosion.bukkit.world.WorldAccessor;
import org.geysermc.erosion.netty.impl.UnixSocketListener;
import org.geysermc.erosion.packet.Packets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ErosionBukkit extends JavaPlugin {
    public static final Map<Player, BukkitPacketHandler> ACTIVE_PLAYERS = new ConcurrentHashMap<>();

    private UnixSocketListener listener;

    @Override
    public void onEnable() {
        ErosionConfig config = ErosionConfig.load(getDataFolder().toPath());
        WorldAccessor worldAccessor = ErosionBukkitUtils.determineWorldAccessor();
        Packets.initBackend();

        listener = new UnixSocketListener();
        listener.createServer(config.getUnixDomainAddress(), () -> new BukkitPacketHandler(getLogger(), worldAccessor));

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.PLUGIN_MESSAGE);
        Bukkit.getPluginManager().registerEvents(new PluginMessageHandler(this), this);

        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(worldAccessor), this);
        Bukkit.getPluginManager().registerEvents(new ErosionPistonListener(worldAccessor), this);
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
