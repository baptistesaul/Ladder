package fr.ladder.api.injector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToInject {
    Class<?> value();
    Type type() default Type.SINGLETON;

    enum Type {
        SINGLETON,
        SCOPED,
        TRANSIENT;
    }
}
