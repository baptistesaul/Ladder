package fr.ladder.core;

import fr.ladder.api.LadderAPI;
import fr.ladder.api.plugin.LadderPlugin;
import fr.ladder.api.util.ReflectionUtils;
import fr.ladder.api.util.WorldUtils;
import fr.ladder.api.injector.Injector;
import fr.ladder.core.injector.LadderInjector;
import fr.ladder.core.util.LadderReflectionUtils;
import fr.ladder.core.util.LadderWorldUtils;
import org.github.paperspigot.PaperSpigotConfig;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class LadderEngine extends LadderPlugin implements LadderAPI.Implementation {

    public LadderEngine() {
        this.implement(LadderAPI.class, this);
        this.implement(Injector.class, new LadderInjector());
        // implement utils
        this.implement(ReflectionUtils.class, new LadderReflectionUtils());
        this.implement(WorldUtils.class, new LadderWorldUtils());
        Injector.setupInjection(this, services -> {

        });
    }

    @Override
    public void onLoad() {
        Injector.runInjection();
        super.onLoad();
        PaperSpigotConfig.strengthEffectModifier = 0.0;
        PaperSpigotConfig.weaknessEffectModifier = 0.0;
    }

    @Override
    public void onEnable() {
        final Logger logger = this.getLogger();
        logger.info("==================[ enabling: " + this.getDescription().getName() + " ]==================");
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

    public void catchException(String errorMessage, Exception e) {
        this.getLogger().severe(errorMessage);
        Throwable cause = e;
        while(cause != null) {
            this.getLogger().severe("- " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
            cause = cause.getCause();
        }
    }
}
