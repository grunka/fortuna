package se.grunka.fortuna.accumulator;

public class EventContext {
    public final EntropySource source;
    public final EventAdder adder;
    public final EventScheduler scheduler;

    EventContext(EntropySource source, EventAdder adder, EventScheduler scheduler) {
        this.source = source;
        this.adder = adder;
        this.scheduler = scheduler;
    }
}
