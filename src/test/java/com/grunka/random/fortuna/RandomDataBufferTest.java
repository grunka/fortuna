package com.grunka.random.fortuna;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class RandomDataBufferTest {

    private ScheduledExecutorService scheduledExecutorService;

    @Before
    public void setUp() {
        scheduledExecutorService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @After
    public void tearDown() {
        scheduledExecutorService.shutdown();
    }

    @Test
    public void testGettingBits() {
        Function<ByteBuffer, ByteBuffer> dataSupplier =
            (i) -> ByteBuffer.wrap(new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef, (byte) 0xfa, (byte) 0xce, (byte) 0xfe, (byte) 0xed});
        RandomDataBuffer randomDataBuffer =
            new RandomDataBuffer(scheduledExecutorService, dataSupplier);
        assertEquals("deadbeef", Integer.toHexString(randomDataBuffer.next(32)));
        assertEquals("f", Integer.toHexString(randomDataBuffer.next(4)));
        assertEquals("ac", Integer.toHexString(randomDataBuffer.next(8)));
        assertEquals("efee", Integer.toHexString(randomDataBuffer.next(16)));
        assertEquals("ddeadbee", Integer.toHexString(randomDataBuffer.next(32)));
        assertEquals("f", Integer.toHexString(randomDataBuffer.next(4)));

        for (int i = 0; i < 32; i++) {
            assertEquals("" + Integer.toBinaryString(0xfacefeed).charAt(i), Integer.toBinaryString(randomDataBuffer.next(1)));
        }
        for (int i = 0; i < 32; i++) {
            assertEquals("" + Integer.toBinaryString(0xdeadbeef).charAt(i), Integer.toBinaryString(randomDataBuffer.next(1)));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfNoDataIsProvided() {
        Function<ByteBuffer, ByteBuffer> dataSupplier =
                (i) -> ByteBuffer.wrap(new byte[0]);
        RandomDataBuffer randomDataBuffer =
            new RandomDataBuffer(scheduledExecutorService, dataSupplier);
        randomDataBuffer.next(1);
    }
}
