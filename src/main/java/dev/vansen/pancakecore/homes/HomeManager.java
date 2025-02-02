package dev.vansen.pancakecore.homes;

import dev.vansen.pancakecore.PancakeCore;
import dev.vansen.pancakecore.homes.util.Home;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Map;

@SuppressWarnings("all")
public class HomeManager {

    public static void createHome(OfflinePlayer player, int index, Location location) {
        PancakeCore.storeHomes().rowOf(player.getUniqueId().toString() + index)
                .value("location", location.serialize())
                .insertAsync();
    }

    public static void deleteHome(OfflinePlayer player, int index) {
        PancakeCore.storeHomes().deleteAsync(player.getUniqueId().toString() + index);
    }

    public static boolean isSet(OfflinePlayer player, int index) {
        return PancakeCore.storeHomes().exists(player.getUniqueId().toString() + index);
    }

    public static Home getHome(OfflinePlayer player, int index) {
        Map<String, Object> data = (Map<String, Object>) PancakeCore.storeHomes()
                .fetch(player.getUniqueId().toString() + index)
                .field("location")
                .getOrDefault(null);
        if (data == null) return null;
        return new Home(index, Location.deserialize(data));
    }
}