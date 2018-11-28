package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulingEntropySource implements EntropySource {

    private Instant lastTime = Instant.now();

    @Override
    public void event(EventAdder adder) {
        Instant now = Instant.now();
        long elapsed = now.isAfter(lastTime)
            ? Duration.between(lastTime, now).toNanos()
            : Duration.between(now, lastTime).toNanos();
        lastTime = now;
        adder.add(Util.twoLeastSignificantBytes(elapsed));
    }

    @Override
    public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
        return scheduler.scheduleWithFixedDelay(runnable, 0, 10, TimeUnit.MILLISECONDS);
    }
}
