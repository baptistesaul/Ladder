package fr.ladder.api.plugin;

import fr.ladder.api.i18n.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class LadderPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        final Logger logger = Bukkit.getLogger();
        logger.info("==================[ loading: " + this.getDescription().getName() + " ]==================");
        super.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        final Logger logger = Bukkit.getLogger();
        logger.info("==================[ enabling: " + this.getDescription().getName() + " ]==================");
        this.getLogger().info("LANG:");
        Messages.loadAllMessages(this);
    }

    @Override
    public void onDisable() {

    }

    public void catchException(String errorMessage, Exception e) {
        this.getLogger().severe(errorMessage);
        Throwable cause = e;
        while(cause != null) {
            this.getLogger().severe("- " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
            cause = cause.getCause();
        }
    }
}
