package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import com.grunka.random.fortuna.accumulator.EventScheduler;

import java.util.concurrent.TimeUnit;

public class FreeMemoryEntropySource implements EntropySource {
    @Override
    public void schedule(EventScheduler scheduler) {
        scheduler.schedule(100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void event(EventAdder adder) {
        long freeMemory = Runtime.getRuntime().freeMemory();
        adder.add(Util.twoLeastSignificantBytes(freeMemory));
    }
}
