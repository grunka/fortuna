package com.grunka.random.fortuna.accumulator;

import java.util.concurrent.TimeUnit;

public interface EventScheduler {
    void schedule(long delay, TimeUnit timeUnit);
}
