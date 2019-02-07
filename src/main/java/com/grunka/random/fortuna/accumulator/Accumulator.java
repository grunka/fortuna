package com.grunka.random.fortuna.accumulator;

import com.grunka.random.fortuna.Pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Accumulator {
    private final Map<Integer, Context> eventContexts = new ConcurrentHashMap<>();
    private final AtomicInteger sourceCount = new AtomicInteger(0);
    private final Pool[] pools;
    private final ScheduledExecutorService scheduler;

    public Accumulator(Pool[] pools, ScheduledExecutorService scheduler) {
        this.pools = pools;
        this.scheduler = scheduler;
    }

    public Pool[] getPools() {
        return pools;
    }

    public void addSource(EntropySource entropySource) {
        int sourceId = sourceCount.getAndIncrement();
        EventAdder eventAdder = new EventAdderImpl(sourceId, pools);
        EventScheduler eventScheduler = new EventSchedulerImpl(sourceId, eventContexts, scheduler);
        Context context = new Context(entropySource, eventAdder, eventScheduler);
        eventContexts.put(sourceId, context);
        eventScheduler.schedule(0, TimeUnit.MILLISECONDS);
    }
}
