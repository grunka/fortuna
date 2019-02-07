package com.grunka.random.fortuna;

import com.grunka.random.fortuna.accumulator.Accumulator;
import com.grunka.random.fortuna.entropy.FreeMemoryEntropySource;
import com.grunka.random.fortuna.entropy.GarbageCollectorEntropySource;
import com.grunka.random.fortuna.entropy.LoadAverageEntropySource;
import com.grunka.random.fortuna.entropy.SchedulingEntropySource;
import com.grunka.random.fortuna.entropy.ThreadTimeEntropySource;
import com.grunka.random.fortuna.entropy.URandomEntropySource;
import com.grunka.random.fortuna.entropy.UptimeEntropySource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Fortuna extends Random {
    private static final int MIN_POOL_SIZE = 64;
    private static final int[] POWERS_OF_TWO = initializePowersOfTwo();

    private static int[] initializePowersOfTwo() {
        int[] result = new int[32];
        for (int power = 0; power < result.length; power++) {
            result[power] = (int) StrictMath.pow(2, power);
        }
        return result;
    }

    private long lastReseedTime = 0;
    private long reseedCount = 0;
    private final RandomDataBuffer randomDataBuffer;
    private final Generator generator;
    private final Accumulator accumulator;
    private final ScheduledExecutorService scheduler;
    private final boolean createdScheduler;

    private static ScheduledExecutorService createDefaultScheduler() {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            private final ThreadFactory delegate = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = delegate.newThread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public static Fortuna createInstance() {
        return new Fortuna();
    }

    @SuppressWarnings("WeakerAccess")
    public static Fortuna createInstance(ScheduledExecutorService scheduler) {
        return new Fortuna(scheduler);
    }

    public Fortuna() {
        ScheduledExecutorService scheduler = createDefaultScheduler();
        this.createdScheduler = true;
        this.generator = new Generator();
        this.randomDataBuffer = new RandomDataBuffer();
        this.accumulator = createAccumulator(scheduler);
        this.scheduler = scheduler;
    }

    public Fortuna(ScheduledExecutorService scheduler) {
        this.createdScheduler = false;
        this.generator = new Generator();
        this.randomDataBuffer = new RandomDataBuffer();
        this.accumulator = createAccumulator(scheduler);
        this.scheduler = scheduler;
    }

    private static Accumulator createAccumulator(ScheduledExecutorService scheduler) {
        Pool[] pools = new Pool[32];
        for (int pool = 0; pool < pools.length; pool++) {
            pools[pool] = new Pool();
        }
        Accumulator accumulator = new Accumulator(pools, scheduler);
        accumulator.addSource(new SchedulingEntropySource());
        accumulator.addSource(new GarbageCollectorEntropySource());
        accumulator.addSource(new LoadAverageEntropySource());
        accumulator.addSource(new FreeMemoryEntropySource());
        accumulator.addSource(new ThreadTimeEntropySource());
        accumulator.addSource(new UptimeEntropySource());
        if (Files.exists(Paths.get("/dev/urandom"))) {
            accumulator.addSource(new URandomEntropySource());
        }
        while (pools[0].size() < MIN_POOL_SIZE) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new Error("Interrupted while waiting for initialization", e);
            }
        }
        return accumulator;
    }

    private byte[] randomData(int bytes) {
        long now = System.currentTimeMillis();
        Pool[] pools = accumulator.getPools();
        if (pools[0].size() >= MIN_POOL_SIZE && now - lastReseedTime > 100) {
            lastReseedTime = now;
            reseedCount++;
            byte[] seed = new byte[pools.length * 32]; // Maximum potential length
            int seedLength = 0;
            for (int pool = 0; pool < pools.length; pool++) {
                if (reseedCount % POWERS_OF_TWO[pool] == 0) {
                    System.arraycopy(pools[pool].getAndClear(), 0, seed, seedLength, 32);
                    seedLength += 32;
                }
            }
            generator.reseed(Arrays.copyOf(seed, seedLength));
        }
        if (reseedCount == 0) {
            throw new IllegalStateException("Generator not reseeded yet");
        } else {
            return generator.pseudoRandomData(bytes);
        }
    }

    @Override
    protected int next(int bits) {
        return randomDataBuffer.next(bits, this::randomData);
    }

    @Override
    public synchronized void setSeed(long seed) {
        // Does not do anything
    }

    @SuppressWarnings("WeakerAccess")
    public void shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        accumulator.shutdownSources();
        if (createdScheduler) {
            scheduler.shutdown();

            if (!scheduler.awaitTermination(timeout, unit)) {
                scheduler.shutdownNow();
            }
        }
    }

    public void shutdown() throws InterruptedException {
        shutdown(30, TimeUnit.SECONDS);
    }
}
