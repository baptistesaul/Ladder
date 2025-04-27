package fr.ladder.core;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Snowtyy
 **/
public class LadderExecutor extends ScheduledThreadPoolExecutor {
    
    public LadderExecutor() {
        super(4);
    }
    
    @NotNull
    @Override
    public Future<?> submit(@NotNull Runnable runnable) {
        return super.submit(() -> {
            try {
                runnable.run();
            } catch(Exception e) {
                this.printStackTrace(e);
            }
        });
    }
    
    @NotNull
    @Override
    public ScheduledFuture<?> schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        return super.schedule(() -> {
            try {
                runnable.run();
            } catch(Exception e) {
                this.printStackTrace(e);
            }
        }, delay, unit);
    }
    
    @NotNull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable runnable, long initialDelay, long period, @NotNull TimeUnit unit) {
        return super.scheduleAtFixedRate(() -> {
            try {
                runnable.run();
            } catch(Exception e) {
                this.printStackTrace(e);
            }
        }, initialDelay, period, unit);
    }
    
    private void printStackTrace(Throwable e) {
        System.err.println("An exception occurred on an Async Thread :");
        e.printStackTrace(System.err);
    }
}
