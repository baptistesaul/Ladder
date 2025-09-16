package fr.ladder.core;

import fr.ladder.api.LadderAPI;
import fr.ladder.api.injector.ScopedServiceCollection;
import fr.ladder.api.plugin.LadderPlugin;
import fr.ladder.api.util.ReflectionUtils;
import fr.ladder.api.util.WorldUtils;
import fr.ladder.api.injector.Injector;
import fr.ladder.core.injector.LadderInjector;
import fr.ladder.core.util.LadderReflectionUtils;
import fr.ladder.core.util.LadderWorldUtils;
import org.github.paperspigot.PaperSpigotConfig;

public class LadderEngine extends LadderPlugin implements LadderAPI.Implementation {

    private final LadderExecutor _executor;

    private final LadderInjector _injector;

    public LadderEngine() {
        _executor = new LadderExecutor();
        // ============ SETUP INJECTOR ============
        _injector = new LadderInjector(this);
        _injector.implement(Injector.class, _injector);
        // ============ SETUP UTILS ===============
        _injector.implement(ReflectionUtils.class, new LadderReflectionUtils());
        _injector.implement(WorldUtils.class, new LadderWorldUtils());
        // ============ SETUP INJECTION ===========
        Injector.setupInjection(this, this::injectAll);
    }

    private void injectAll(ScopedServiceCollection services) {
        services.addScoped(_executor);
    }

    @Override
    public void onLoad() {
        _injector.runInjection();
        super.onLoad();
        PaperSpigotConfig.strengthEffectModifier = 0.0;
        PaperSpigotConfig.weaknessEffectModifier = 0.0;
    }
}
