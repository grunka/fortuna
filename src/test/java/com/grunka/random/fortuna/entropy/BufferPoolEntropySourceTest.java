package com.grunka.random.fortuna.entropy;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class BufferPoolEntropySourceTest {

    private BufferPoolEntropySource target;
    private int adds;

    @Before
    public void before() {
        target = new BufferPoolEntropySource();
        adds = 0;
        ByteBuffer.allocateDirect(300);
    }

    @Test
    public void shouldGetBufferPoolData() {
        target.event(
                event -> {
                    assertEquals(2, event.length);
                    adds++;
                }
        );
        assertEquals(1, adds);
    }


}
