package fr.ladder.core.util.graph;

import fr.ladder.api.util.graph.IGraph;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ClassGraphAdapter implements IGraph {

    private final ScanResult _result;

    public ClassGraphAdapter(JavaPlugin plugin, ClassGraph graph) {
        Instant start = Instant.now();
        _result = graph.scan();
        Duration duration = Duration.between(start, Instant.now());
        double durationInMs = (duration.toNanos() / 10000D) / 100D;
        if(durationInMs > 100) {
            plugin.getLogger().info("Plugin scan completed!");
            plugin.getLogger().warning("> time: " + (duration.toNanos() / 10000) / 100D + "ms");
        }
    }

    @Override
    public Stream<String> getResources(Pattern pattern) {
        return _result.getResourcesMatchingPattern(pattern).stream()
                .map(Resource::getPath);
    }

    public Stream<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        return _result.getClassesWithAnnotation(annotation).stream()
                .map(ClassInfo::loadClass);
    }

    public Stream<Field> getFieldWithAnnotation(Class<? extends Annotation> annotation) {
        return _result.getClassesWithFieldAnnotation(annotation).stream()
                .map(ClassInfo::loadClass)
                .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
                .filter(f -> f.isAnnotationPresent(annotation));
    }

    @Override
    public void close() {
        _result.close();
    }
}
