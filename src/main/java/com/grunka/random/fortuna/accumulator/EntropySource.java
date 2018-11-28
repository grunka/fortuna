package com.grunka.random.fortuna.accumulator;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public interface EntropySource {

    void event(EventAdder adder);

    Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler);
}
