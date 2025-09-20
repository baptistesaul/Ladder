package fr.ladder.api;

import fr.ladder.api.game.GameHandler;
import org.bukkit.Bukkit;

import java.util.Random;

public abstract class LadderAPI {

    private static Implementation impl;

    public static final Random RANDOM = new Random();

    public static GameHandler getGameHandler() {
        return impl.getGameHandler();
    }

    public static void catchException(String errorMessage, Throwable ex) {
        Bukkit.getLogger().severe(errorMessage);
        Throwable cause = ex;
        while(cause != null) {
            Bukkit.getLogger().severe("- " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
            cause = cause.getCause();
        }
    }

    public interface Implementation {

        GameHandler getGameHandler();

    }
}
