package fr.ladder.api.plugin;

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
    }

    @Override
    public void onDisable() {

    }
}
