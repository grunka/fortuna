package com.grunka.random.fortuna;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class PrefetchingSupplierTest {
    private ExecutorService executorService;
    private List<Integer> sleeps;

    @Before
    public void setUp() {
        sleeps = new ArrayList<>(Arrays.asList(200, 150, 100, 50, 0));
        executorService = Executors.newFixedThreadPool(5);
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

    @Test
    public void shouldBeOrderedAndCorrectNumberOfOutputs() throws ExecutionException, InterruptedException {
        AtomicInteger number = new AtomicInteger();
        PrefetchingSupplier<Integer> prefetcher = new PrefetchingSupplier<>(() -> {
            sleep();
            return number.getAndIncrement();
        }, executorService);
        List<Integer> values = new ArrayList<>();
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(executorService.submit(() -> values.add(prefetcher.get())));
        }
        for (Future<?> future : futures) {
            future.get();
        }
        assertEquals(Arrays.asList(0, 1, 2, 3, 4), values);
    }

    private void sleep() {
        try {
            Thread.sleep(sleeps.remove(0));
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }
}
