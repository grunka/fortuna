package se.grunka.fortuna;

public interface EntropySource {

    void event(EventScheduler scheduler, EventAdder adder);
}
