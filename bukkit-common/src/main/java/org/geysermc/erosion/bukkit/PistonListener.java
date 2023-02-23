package org.geysermc.erosion.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public abstract class PistonListener implements Listener {

    // The handlers' parent class cannot be registered
    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPistonExtend(BlockPistonExtendEvent event) {
        onPistonAction(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onPistonRetract(BlockPistonRetractEvent event) {
        onPistonAction(event);
    }

    private void onPistonAction(BlockPistonEvent event) {
        if (event.isCancelled()) {
            return;
        }

        onPistonAction0(event);
    }

    protected abstract void onPistonAction0(BlockPistonEvent event);
}
