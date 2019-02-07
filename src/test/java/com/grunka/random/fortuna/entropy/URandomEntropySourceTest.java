package com.grunka.random.fortuna.entropy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class URandomEntropySourceTest {

    private URandomEntropySource target;
    private int adds;

    @Before
    public void before() {
        target = new URandomEntropySource();
        adds = 0;
    }

    @Test
    public void shouldAddUptime() {
        target.event(event -> {
            assertEquals(32, event.length);
            adds++;
        });
        assertEquals(1, adds);
    }
}
