package com.grunka.random.fortuna.accumulator;

public interface EntropySource {

    void event(EventScheduler scheduler, EventAdder adder);
}
