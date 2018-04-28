package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.accumulator.EventAdder;
import com.grunka.random.fortuna.accumulator.EventScheduler;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class GarbageCollectorEntropySourceTest {

    private GarbageCollectorEntropySource target;
    private int schedules;
    private int adds;

    @Before
    public void before() throws Exception {
        target = new GarbageCollectorEntropySource();
        schedules = 0;
        adds = 0;
    }

    @Test
    public void shouldGetGarbageCollectionData() throws Exception {
        target.event(
                new EventScheduler() {
                    @Override
                    public void schedule(long delay, TimeUnit timeUnit) {
                        assertEquals(TimeUnit.SECONDS.toMillis(10), timeUnit.toMillis(delay));
                        schedules++;
                    }
                },
                new EventAdder() {
                    @Override
                    public void add(byte[] event) {
                        assertEquals(2, event.length);
                        adds++;
                    }
                }
        );
        assertEquals(1, schedules);
        assertEquals(1, adds);
    }
}
