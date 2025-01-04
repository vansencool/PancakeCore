package dev.vansen.pancakecore.homes.util;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Home {
    private int index;
    private Location location;

    public Home(int index, @NotNull Location location) {
        this.index = index;
        this.location = location;
    }

    public int getIndex() {
        return index;
    }

    public Home setIndex(int index) {
        this.index = index;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public Home setLocation(@NotNull Location location) {
        this.location = location;
        return this;
    }
}
