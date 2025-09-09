package fr.ladder.core.injector;

import fr.ladder.api.injector.Injector;
import fr.ladder.api.injector.ScopedServiceCollection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * @author Snowtyy
 */
public class LadderInjector implements Injector.Implementation {

    private Set<JavaPlugin> _plugins;

    private final LadderServiceCollection _serviceCollection;

    public LadderInjector() {
        _plugins = new HashSet<>();
        _serviceCollection = new LadderServiceCollection();
    }

    @Override
    public void setupInjection(JavaPlugin plugin, Consumer<ScopedServiceCollection> consumer) {
        _plugins.add(plugin);
        _serviceCollection.addAll(plugin);
        consumer.accept(new WrapperServiceCollection(plugin, _serviceCollection));
    }

    @Override
    public void runInjection() {
        final Logger logger = Bukkit.getLogger();
        logger.info("Injecting dependencies...");
        Instant start = Instant.now();
        _plugins.forEach(_serviceCollection::injectAll);
        Duration duration = Duration.between(start, Instant.now());
        logger.info("Injection finished!");
        logger.info("> time: " + (duration.toNanos() / 10000) / 100D + "ms");
    }
}
