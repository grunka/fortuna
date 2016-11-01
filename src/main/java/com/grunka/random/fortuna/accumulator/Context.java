package com.grunka.random.fortuna.accumulator;

class Context {
    final EntropySource source;
    public final EventAdder adder;
    public final EventScheduler scheduler;

    Context(EntropySource source, EventAdder adder, EventScheduler scheduler) {
        this.source = source;
        this.adder = adder;
        this.scheduler = scheduler;
    }
}
