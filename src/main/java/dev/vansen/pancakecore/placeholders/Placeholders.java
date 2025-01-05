package dev.vansen.pancakecore.placeholders;

import dev.vansen.pancakecore.PancakeCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return "vansen"; //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "pancakecore"; //
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0"; //
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "balance" -> {
                return String.valueOf(PancakeCore.economy()
                        .getBalance(player));
            }
            case "tps" -> {
                return String.valueOf(Bukkit.getServer().getTPS().length > 0 ? Bukkit.getServer().getTPS()[0] : 20.0);
            }
            case "ping" -> {
                if (player instanceof Player online) return String.valueOf(online.getPing());
            }
        }
        return "? ? ?";
    }
}
