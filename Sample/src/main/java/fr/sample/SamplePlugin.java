package fr.sample;

import fr.ladder.api.injector.annotation.Inject;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SamplePlugin extends JavaPlugin {

    @Inject
    private static Logger logger;

    public SamplePlugin() {
        System.out.println("Instantiating Sample Plugin...");
    }

    @Override
    public void onLoad() {
        System.out.println("Loading Sample Plugin...");

    }

    @Override
    public void onEnable() {
        System.out.println("Enabling Sample Plugin...");
    }


}
