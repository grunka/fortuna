package com.grunka.random.fortuna.entropy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoadAverageEntropySourceTest {

    private LoadAverageEntropySource target;
    private int adds;

    @Before
    public void before() {
        target = new LoadAverageEntropySource();
        adds = 0;
    }

    @Test
    public void shouldAddTwoBytesAndSchedule() {
        target.event(
            event -> {
                assertEquals(2, event.length);
                adds++;
            }
        );
        assertEquals(1, adds);
    }
}
