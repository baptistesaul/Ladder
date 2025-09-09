package fr.ladder.core.util;

import fr.ladder.api.util.ReflectionUtils;
import fr.ladder.api.util.graph.IGraph;
import fr.ladder.core.util.graph.ClassGraphAdapter;
import io.github.classgraph.ClassGraph;
import org.bukkit.plugin.java.JavaPlugin;

public class LadderReflectionUtils implements ReflectionUtils.Implementation {

    @Override
    public IGraph getGraph(JavaPlugin plugin) {
        ClassGraph graph = new ClassGraph()
                .addClassLoader(plugin.getClass().getClassLoader())
                .enableAllInfo();

        return new ClassGraphAdapter(plugin, graph);
    }

}
