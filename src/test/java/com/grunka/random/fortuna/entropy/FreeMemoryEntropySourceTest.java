package com.grunka.random.fortuna.entropy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FreeMemoryEntropySourceTest {

    private FreeMemoryEntropySource target;
    private int adds;

    @Before
    public void before() {
        target = new FreeMemoryEntropySource();
        adds = 0;
    }

    @Test
    public void shouldReadFreeMemory() {
        target.event(
            event -> {
                assertEquals(2, event.length);
                adds++;
            }
        );
        assertEquals(1, adds);
    }
}
