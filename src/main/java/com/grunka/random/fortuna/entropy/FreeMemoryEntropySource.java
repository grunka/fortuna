package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FreeMemoryEntropySource implements EntropySource {
    @Override
    public void event(EventAdder adder) {
        long freeMemory = Runtime.getRuntime().freeMemory();
        adder.add(Util.twoLeastSignificantBytes(freeMemory));
    }

    @Override
    public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
        return scheduler.scheduleWithFixedDelay(runnable, 0, 100, TimeUnit.MILLISECONDS);
    }
}
