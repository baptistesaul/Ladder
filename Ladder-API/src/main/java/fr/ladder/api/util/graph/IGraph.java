package fr.ladder.api.util.graph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface IGraph extends AutoCloseable {

    Stream<String> getResources(Pattern pattern);

    Stream<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation);

    Stream<Field> getFieldWithAnnotation(Class<? extends Annotation> annotation);

    @Override
    void close();
}
