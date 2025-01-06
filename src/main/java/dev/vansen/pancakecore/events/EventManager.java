package dev.vansen.pancakecore.events;

import dev.vansen.pancakecore.PancakeCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class EventManager {

    @SafeVarargs
    public static void register(@NotNull Class<? extends Listener>... classes) {
        try {
            for (Class<? extends Listener> clazz : classes) {
                Bukkit.getPluginManager().registerEvents(clazz.getDeclaredConstructor().newInstance(), PancakeCore.plugin());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
