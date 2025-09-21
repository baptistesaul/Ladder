package fr.ladder.api;

import fr.ladder.api.game.GameHandler;
import org.bukkit.Bukkit;

import java.util.Random;
import java.util.logging.Logger;

public abstract class LadderAPI {

    private static Implementation impl;

    public static final Random RANDOM = new Random();

    public static GameHandler getGameHandler() {
        return impl.getGameHandler();
    }

    public static void catchException(Logger logger, String errorMessage, Throwable ex) {
        logger.severe(errorMessage);
        Throwable cause = ex;
        while(cause != null) {
            logger.severe("- " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
            cause = cause.getCause();
        }
    }

    public static void catchException(String errorMessage, Throwable ex) {
        catchException(Bukkit.getLogger(), errorMessage, ex);
    }

    public interface Implementation {

        GameHandler getGameHandler();

    }
}
