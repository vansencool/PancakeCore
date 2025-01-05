package dev.vansen.pancakecore.events.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockBreak implements Listener {

    @EventHandler
    public void blockBreak(@NotNull BlockBreakEvent event) {
        if (event.getPlayer().getLocation().getWorld().getName().equals("spawn")) {
            event.setCancelled(true);
        }
    }
}
