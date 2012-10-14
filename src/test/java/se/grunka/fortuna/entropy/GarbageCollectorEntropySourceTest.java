package se.grunka.fortuna.entropy;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import se.grunka.fortuna.accumulator.EventAdder;
import se.grunka.fortuna.accumulator.EventScheduler;

import static org.junit.Assert.assertEquals;

public class GarbageCollectorEntropySourceTest {

    private GarbageCollectorEntropySource target;

    @Before
    public void before() throws Exception {
        target = new GarbageCollectorEntropySource();
    }

    @Test
    public void shouldGetGarbageCollectionData() throws Exception {
        target.event(
                new EventScheduler() {
                    @Override
                    public void schedule(long delay, TimeUnit timeUnit) {
                        assertEquals(TimeUnit.SECONDS.toMillis(10), timeUnit.toMillis(delay));
                    }
                },
                new EventAdder() {
                    @Override
                    public void add(byte[] event) {
                        assertEquals(2, event.length);
                    }
                }
        );
    }
}
