package com.grunka.random.fortuna.accumulator;

public interface EntropySource {
    void schedule(EventScheduler scheduler);

    void event(EventAdder adder);
}
