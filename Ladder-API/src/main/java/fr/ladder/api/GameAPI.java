package fr.ladder.api;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class GameAPI {

    private static Implementation implementation;

    public static JavaPlugin getPlugin() {
        return implementation.getPlugin();
    }

    interface Implementation {

        JavaPlugin getPlugin();

    }
}
