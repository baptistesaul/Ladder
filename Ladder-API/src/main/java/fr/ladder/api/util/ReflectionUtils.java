package fr.ladder.api.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

public abstract class ReflectionUtils {

    private static Implementation implementation;

    public static Reflections getReflector(JavaPlugin plugin) {
        return implementation.getReflector(plugin);
    }

    public interface Implementation {

        Reflections getReflector(JavaPlugin plugin);

    }

}
