package com.grunka.random.fortuna;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.ISAACRandom;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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
        int numbers = 1_000;
        SummaryStatistics fortunaNumbers = new SummaryStatistics();
        SummaryStatistics isaacNumbers = new SummaryStatistics();
        SummaryStatistics mersenneNumbers = new SummaryStatistics();
        Fortuna fortuna = Fortuna.createInstance();
        ISAACRandom isaacRandom = new ISAACRandom();
        MersenneTwister mersenneTwister = new MersenneTwister();
        for (int i = 0; i < 10_000_000; i++) {
            fortunaNumbers.addValue(fortuna.nextInt(numbers));
            isaacNumbers.addValue(isaacRandom.nextInt(numbers));
            mersenneNumbers.addValue(mersenneTwister.nextInt(numbers));
        }
        double varFortuna = fortunaNumbers.getVariance();
        double varIsaac = isaacNumbers.getVariance();
        double varMersenne = mersenneNumbers.getVariance();
        double varUni = new UniformRealDistribution(0, numbers).getNumericalVariance();
        double percentDifferenceFortuna = (varFortuna - varUni) / varUni;
        double percentDifferenceIsaac = (varIsaac - varUni) / varUni;
        double percentDifferenceMersenne = (varMersenne - varUni) / varUni;
        System.out.println("Variances: Fortuna " + varFortuna + ", ISAAC " + varIsaac + ", Mersenne " + varMersenne + ", Uniform " + varUni);
        System.out.println("UniformRealDistribution vs Fortuna variance difference percent: " + percentDifferenceFortuna * 100 + " %");
        System.out.println("UniformRealDistribution vs ISAAC variance difference percent: " + percentDifferenceIsaac * 100 + " %");
        System.out.println("UniformRealDistribution vs Mersenne variance difference percent: " + percentDifferenceMersenne * 100 + " %");
        assertEquals("UniformRealDistribution vs Fortuna variance", 0.0, percentDifferenceFortuna, 0.01);
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
            throw new UnsupportedOperationException();
        }

        List<ScheduledFuture<?>> getCreatedFutures() {
            return futures;
        }
    }
}
