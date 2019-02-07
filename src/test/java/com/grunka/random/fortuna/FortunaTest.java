package com.grunka.random.fortuna;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FortunaTest {
    @Test
    public void shouldCreateInstanceAndWaitForInitialization() {
        Fortuna fortuna = Fortuna.createInstance();
        try {
            fortuna.nextInt(42);
        } catch (IllegalStateException ignored) {
            fail("Did not wait for initialization");
        }
    }

    @Test
    public void shouldProduceEvenDistribution() {
        int[] numbers = new int[10];
        Fortuna fortuna = Fortuna.createInstance();
        for (int i = 0; i < 100_000_000; i++) {
            numbers[fortuna.nextInt(10)]++;
        }
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int number : numbers) {
            if (number > highest) {
                highest = number;
            }
            if (number < lowest) {
                lowest = number;
            }
        }
        System.out.println("numbers = " + Arrays.toString(numbers));
        int percentage = (100 * (highest - lowest)) / lowest;
        System.out.println("percentage = " + percentage);
        assertEquals(0, percentage);
    }

    @Test
    public void shouldNotShutdownPassedInScheduledExecutorService() throws InterruptedException {
        ScheduledExecutorService delegate = Executors.newSingleThreadScheduledExecutor();
        try {
            MockScheduledExecutorService scheduler = new MockScheduledExecutorService(delegate);
            Fortuna fortuna = Fortuna.createInstance(scheduler);
            fortuna.shutdown();
            scheduler.getCreatedFutures().forEach(f -> assertTrue("Future was not cancelled", f.isCancelled()));
            assertFalse("Scheduler was shut down", scheduler.isShutdown());
        } finally {
            delegate.shutdown();
        }
    }

    private static class MockScheduledExecutorService implements ScheduledExecutorService {
        private final ScheduledExecutorService delegate;
        private final List<ScheduledFuture<?>> futures = new ArrayList<>();

        MockScheduledExecutorService(ScheduledExecutorService delegate) {
            this.delegate = delegate;
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            ScheduledFuture<?> future = delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
            futures.add(future);
            return future;
        }

        @Override
        public void shutdown() {
            delegate.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Future<?> submit(Runnable task) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void execute(Runnable command) {
            throw new UnsupportedOperationException();
        }

        List<ScheduledFuture<?>> getCreatedFutures() {
            return futures;
        }
    }
}
