package fr.ladder.core;

import fr.ladder.api.LadderAPI;
import fr.ladder.api.util.ReflectionUtils;
import fr.ladder.api.util.WorldUtils;
import fr.ladder.core.util.LadderReflectionUtils;
import fr.ladder.core.util.LadderWorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class LadderEngine extends JavaPlugin implements LadderAPI.Implementation {

    public LadderEngine() {
        this.implement(LadderAPI.class, this);
        this.implement(ReflectionUtils.class, new LadderReflectionUtils());
        this.implement(WorldUtils.class, new LadderWorldUtils());
    }

    @Override
    public void onLoad() {
        final Logger logger = Bukkit.getLogger();
        logger.info("==================[ loading: " + this.getDescription().getName() + " ]==================");
    }

    @Override
    public void onEnable() {
        final Logger logger = this.getLogger();
        logger.info("==================[ enabling: " + this.getDescription().getName() + " ]==================");
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return null;
    }

    @Override
    public void implement(Class<?> clazz, Object instance) {
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            if(field.getType().isAssignableFrom(instance.getClass())) {
                try {
                    field.setAccessible(true);
                    field.set(null, instance);
                } catch (IllegalAccessException e) {
                    this.catchException("An error occurred on implement: " + clazz.getSimpleName(), e);
                }
            }
        }
    }

    @Override
    public void catchException(String errorMessage, Exception e) {
        this.getLogger().severe(errorMessage);
        Throwable cause = e;
        while(cause != null) {
            this.getLogger().severe("- " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
            cause = cause.getCause();
        }
    }
}
