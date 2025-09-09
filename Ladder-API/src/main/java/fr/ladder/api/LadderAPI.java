package fr.ladder.api;

import java.util.Random;

public abstract class LadderAPI {

    private static Implementation impl;

    public static final Random RANDOM = new Random();

    public interface Implementation {

        void implement(Class<?> clazz, Object instance);

    }
}
