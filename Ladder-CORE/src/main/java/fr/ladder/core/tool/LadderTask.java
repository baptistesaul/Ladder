package fr.ladder.core.tool;

import fr.ladder.api.injector.annotation.Inject;
import fr.ladder.api.tool.Task;
import fr.ladder.api.tool.scheduler.AsyncRunnable;
import fr.ladder.core.LadderEngine;
import fr.ladder.core.LadderExecutor;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LadderTask implements Task.Implementation {

    @Inject
    private static LadderEngine _engine;

    @Inject
    private static LadderExecutor _executor;
    
    private final AtomicInteger lastId = new AtomicInteger(0);
    
    private final Map<Integer, Future<?>> asyncTaskMap = new ConcurrentHashMap<>();
    
    @Override
    public void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(_engine, runnable);
    }
    
    @Override
    public void runAsync(Runnable runnable) {
        _executor.schedule(runnable, 5, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public synchronized int run(Runnable runnable, long tick) {
        return Bukkit.getScheduler().runTaskLater(_engine, runnable, tick).getTaskId();
    }
    
    @Override
    public synchronized int runAsync(Runnable runnable, long tick) {
        ScheduledFuture<?> future = _executor.schedule(runnable, tick * 50, TimeUnit.MILLISECONDS);
        int taskId = lastId.decrementAndGet();
        this.asyncTaskMap.put(taskId, future);
        
        return taskId;
    }
    
    @Override
    public synchronized void runAsync(AsyncRunnable runnable, long delay, long period) {
        ScheduledFuture<?> future = _executor.scheduleAtFixedRate(runnable,
                delay * 50,
                period * 50,
                TimeUnit.MILLISECONDS
        );
        int taskId = lastId.decrementAndGet();
        this.asyncTaskMap.put(taskId, future);
        runnable.setupId(taskId);
    }
    
    @Override
    public synchronized boolean isRunning(int taskId) {
        return this.asyncTaskMap.containsKey(taskId);
    }
    
    @Override
    public synchronized void cancel(int taskId) {
        Future<?> task = this.asyncTaskMap.remove(taskId);
        if(task == null)
            return;
        task.cancel(true);
    }

}
