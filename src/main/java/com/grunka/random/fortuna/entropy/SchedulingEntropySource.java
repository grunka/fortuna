package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import com.grunka.random.fortuna.accumulator.EventScheduler;

import java.util.concurrent.TimeUnit;

public class SchedulingEntropySource implements EntropySource {
    private long lastTime = 0;

    @Override
    public void schedule(EventScheduler scheduler) {
        scheduler.schedule(10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void event(EventAdder adder) {
        long now = System.nanoTime();
        long elapsed = now - lastTime;
        lastTime = now;
        adder.add(Util.twoLeastSignificantBytes(elapsed));
    }
}
