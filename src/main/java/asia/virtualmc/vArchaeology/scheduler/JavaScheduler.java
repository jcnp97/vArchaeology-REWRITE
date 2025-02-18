package asia.virtualmc.vArchaeology.scheduler;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaScheduler {

    private final ScheduledExecutorService scheduler;
    private final ForkJoinPool worker;

    public JavaScheduler() {
        // Create a ScheduledExecutorService with a custom thread factory.
        this.scheduler = Executors.newScheduledThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new ThreadFactory() {
                    private final AtomicInteger count = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "JavaSchedulerThread-" + count.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                }
        );

        // Create a ForkJoinPool to run tasks asynchronously.
        this.worker = new ForkJoinPool(
                Runtime.getRuntime().availableProcessors(),
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                false
        );
    }

    /**
     * Schedules a repeating asynchronous task.
     *
     * @param task The Runnable to execute.
     * @param delay The initial delay before execution.
     * @param interval The period between successive executions.
     * @param unit The time unit of delay and interval.
     * @return A SchedulerTask which can be used to cancel the task.
     */
    public SchedulerTask asyncRepeating(Runnable task, long delay, long interval, TimeUnit unit) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> worker.execute(task),
                delay,
                interval,
                unit
        );
        return () -> future.cancel(false);
    }

    /**
     * Shutdown the scheduler and worker pool.
     */
    public void shutdown() {
        scheduler.shutdownNow();
        worker.shutdown();
    }

    /**
     * Functional interface representing a cancelable scheduled task.
     */
    public interface SchedulerTask {
        void cancel();
    }
}
