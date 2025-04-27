package fr.ladder.core.util;

import fr.ladder.api.util.ReflectionUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LadderReflectionUtils implements ReflectionUtils.Implementation {

    private final Map<Class<? extends JavaPlugin>, Reflections> reflectorMap = new HashMap<>();

    @Override
    public Reflections getReflector(JavaPlugin plugin) {
        Class<? extends JavaPlugin> clazz = plugin.getClass();
        if(this.reflectorMap.containsKey(clazz)) {
            return reflectorMap.get(clazz);
        }

        Instant start = Instant.now();
        ConfigurationBuilder config = new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage(plugin.getClass().getPackageName(), plugin.getClass().getClassLoader()))
                .setScanners(Scanners.values());
        this.reflectorMap.put(clazz, new Reflections(config));
        Duration duration = Duration.between(start, Instant.now());
        plugin.getLogger().info("Plugin scan completed!");
        plugin.getLogger().info("> time: " + (duration.toNanos() / 10000) / 100D + "ms");

        return this.reflectorMap.get(clazz);
    }

}
