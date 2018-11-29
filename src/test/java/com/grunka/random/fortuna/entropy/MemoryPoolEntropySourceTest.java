package com.grunka.random.fortuna.entropy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MemoryPoolEntropySourceTest {

    private MemoryPoolEntropySource target;
    private int adds;

    @Before
    public void before() {
        target = new MemoryPoolEntropySource();
        adds = 0;
    }

    @Test
    public void shouldGetMemoryPoolData() {
        target.event(
            event -> {
                assertEquals(2, event.length);
                adds++;
            }
        );
        assertEquals(1, adds);
    }


}
