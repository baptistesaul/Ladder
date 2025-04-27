package fr.ladder.api.tool.scheduler;

import fr.ladder.api.tool.Task;

public abstract class AsyncRunnable implements Runnable {

    private int id = 0;

    public void setupId(int newId) {
        this.id = newId;
    }

    public synchronized void cancel() {
        Task.cancel(this.id);
    }

    public synchronized boolean isRunning() {
        return Task.isRunning(this.id);
    }

    public synchronized void runAsynchronously(long delay, long period) {
        Task.runAsync(this, delay, period);
    }

}
