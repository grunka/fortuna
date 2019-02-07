package com.grunka.random.fortuna.entropy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ThreadTimeEntropySourceTest {

    private ThreadTimeEntropySource target;
    private int adds;

    @Before
    public void before() {
        target = new ThreadTimeEntropySource();
        adds = 0;
    }

    @Test
    public void shouldAddBytes() {
        target.event(event -> {
            assertEquals(2, event.length);
            adds++;
        });
        assertEquals(1, adds);
    }
}
