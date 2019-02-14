package com.grunka.random.fortuna;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class PrefetchingSupplierTest {
    private ExecutorService executorService;

    @Before
    public void setUp() {
        executorService = Executors.newFixedThreadPool(2);
    }

    @After
    public void tearDown() {
        executorService.shutdown();
    }

    @Test
    public void shouldGetValues() {
        AtomicInteger number = new AtomicInteger();
        PrefetchingSupplier<String> prefetcher = new PrefetchingSupplier<>(() -> "hello " + number.getAndIncrement(), executorService);
        assertEquals("hello 0", prefetcher.get());
        assertEquals("hello 1", prefetcher.get());
        assertEquals("hello 2", prefetcher.get());
    }
}
