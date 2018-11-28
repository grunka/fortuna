package com.grunka.random.fortuna.entropy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GarbageCollectorEntropySourceTest {

    private GarbageCollectorEntropySource target;
    private int adds;

    @Before
    public void before() {
        target = new GarbageCollectorEntropySource();
        adds = 0;
    }

    @Test
    public void shouldGetGarbageCollectionData() {
        target.event(
            event -> {
                assertEquals(2, event.length);
                adds++;
            }
        );
        assertEquals(1, adds);
    }
}
