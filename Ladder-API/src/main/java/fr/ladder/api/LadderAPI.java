package fr.ladder.api;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public abstract class LadderAPI {

    private static Implementation impl;

    public static final Random RANDOM = new Random();

    public static <T> T get(Class<T> clazz) {
        return impl.get(clazz);
    }

    public static void catchException(String errorMessage, Exception e) {
        impl.catchException(errorMessage, e);
    }

    public static void implement(Class<?> clazz, Object instance) { impl.implement(clazz, instance); }

    public interface Implementation {

        <T> T get(Class<T> clazz);

        void catchException(String errorMessage, Exception e);

        void implement(Class<?> clazz, Object instance);
    }
}
