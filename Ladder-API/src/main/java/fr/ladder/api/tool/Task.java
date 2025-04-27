package fr.ladder.api.tool;

import fr.ladder.api.tool.scheduler.AsyncRunnable;

public abstract class Task {
    
    private static Implementation implementation;
    
    public static void run(Runnable runnable) {
        implementation.run(runnable);
    }
    
    public static void runAsync(Runnable runnable) {
        implementation.runAsync(runnable);
    }
    
    public static int run(Runnable runnable, long tick) {
        return implementation.run(runnable, tick);
    }
    
    public static int runAsync(Runnable runnable, long tick) {
        return implementation.runAsync(runnable, tick);
    }

    public static void runAsync(AsyncRunnable runnable, long delay, long period) {
        implementation.runAsync(runnable, delay, period);
    }

    public static boolean isRunning(int taskId) {
        return implementation.isRunning(taskId);
    }
    
    public static void cancel(int taskId) {
        implementation.cancel(taskId);
    }
    
    public interface Implementation {
        
        void run(Runnable runnable);
        
        void runAsync(Runnable runnable);
        
        int run(Runnable runnable, long tick);
        
        int runAsync(Runnable runnable, long tick);

        void runAsync(AsyncRunnable runnable, long delay, long period);
        
        boolean isRunning(int taskId);
        
        void cancel(int taskId);
        
    }
}
