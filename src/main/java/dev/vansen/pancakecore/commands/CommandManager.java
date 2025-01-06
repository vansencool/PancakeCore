package dev.vansen.pancakecore.commands;

import org.jetbrains.annotations.NotNull;

public class CommandManager {

    public static void register(@NotNull Class<?>... classes) {
        for (Class<?> clazz : classes) {
            try {
                clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
