package com.grunka.random.fortuna;

import org.junit.Ignore;
import org.junit.Test;

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

    @Ignore
    @Test
    public void shouldProduceEvenDistribution() {
        int[] numbers = new int[10];
        Fortuna fortuna = Fortuna.createInstance();
        for (int i = 0; i < 1000000; i++) {
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
        ScheduledExecutorService scheduler = new ScheduledExecutorService() {
            @Override
            public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
                return delegate.schedule(command, delay, unit);
            }

            @Override
            public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
                return delegate.schedule(callable, delay, unit);
            }

            @Override
            public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
                return delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
            }

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
                return delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
            }

            @Override
            public void shutdown() {
                fail("Tried to shutdown");
                throw new UnsupportedOperationException();
            }

            @Override
            public List<Runnable> shutdownNow() {
                fail("Tried to shutdown");
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isShutdown() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isTerminated() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) {
                fail("Waiting for termination");
                return false;
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return delegate.submit(task);
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                return delegate.submit(task, result);
            }

            @Override
            public Future<?> submit(Runnable task) {
                return delegate.submit(task);
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
                delegate.execute(command);
            }
        };
        Fortuna fortuna = Fortuna.createInstance(scheduler);
        fortuna.shutdown();
        delegate.shutdown();
    }
}
