package dev.vansen.pancakecore.events.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class BlockPlace implements Listener {

    @EventHandler
    public void blockPlace(@NotNull BlockPlaceEvent event) {
        if (event.getPlayer().getLocation().getWorld().getName().equals("spawn")) {
            event.setCancelled(true);
        }
    }
}
