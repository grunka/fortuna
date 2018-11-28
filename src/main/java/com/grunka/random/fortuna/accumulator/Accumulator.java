package com.grunka.random.fortuna.accumulator;

import com.grunka.random.fortuna.Pool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Accumulator {

    private final Set<Future<?>> futures = new HashSet<>();
    private final AtomicInteger sourceCount = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler;
    private final Pool[] pools;

    public Accumulator(Pool[] pools, ScheduledExecutorService scheduler) {
        this.pools = pools;
        this.scheduler = scheduler;
    }

    public Pool[] getPools() {
        return pools;
    }

    public void addSource(EntropySource entropySource) {
        int sourceId = sourceCount.getAndIncrement();
        EventAdder eventAdder = new EventAdderImpl(sourceId, pools, entropySource.getClass());
        futures.add(
            entropySource.schedule(() -> entropySource.event(eventAdder), scheduler)
        );
    }

    public void shutdown(long timeout, TimeUnit unit, boolean shutdownExecutor) throws InterruptedException {
        for (Future<?> future : futures) {
            future.cancel(true);
        }
        futures.clear();
        if (shutdownExecutor) {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(timeout, unit)) {
                scheduler.shutdownNow();
            }
        }
    }
}
