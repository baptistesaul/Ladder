package fr.ladder.api.util;

import fr.ladder.api.util.graph.IGraph;
import org.bukkit.plugin.java.JavaPlugin;


public abstract class ReflectionUtils {

    private static Implementation implementation;

    public static IGraph getGraph(JavaPlugin plugin) {
        return implementation.getGraph(plugin);
    }

    public interface Implementation {

        IGraph getGraph(JavaPlugin plugin);

    }

}
