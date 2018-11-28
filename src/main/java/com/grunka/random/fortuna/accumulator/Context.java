package com.grunka.random.fortuna.accumulator;

class Context {
    final EntropySource source;
    public final EventAdder adder;

    Context(EntropySource source, EventAdder adder) {
        this.source = source;
        this.adder = adder;
    }
}
