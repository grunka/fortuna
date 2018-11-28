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
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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

    private Instant lastReseedTime = Instant.now();
    private final AtomicLong reseedCount = new AtomicLong(0);
    private final RandomDataBuffer randomDataBuffer;
    private final Generator generator;
    private final Accumulator accumulator;
    private boolean shutdownExecutor = false;

    public static Fortuna createInstance() {
        final ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(
                    Math.min(3, Runtime.getRuntime().availableProcessors()),
                    new ThreadFactory() {
                        private final ThreadFactory delegate = Executors.defaultThreadFactory();
                        private final AtomicLong threadCounter = new AtomicLong(0);
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = delegate.newThread(r);
                            thread.setName("fortuna-entropy-collector-"+threadCounter.getAndIncrement());
                            thread.setDaemon(true);
                            return thread;
                        }
                    }
        );
        Fortuna instance = createInstance(scheduledExecutorService);
        instance.setShutdownExecutor(true);
        return instance;
    }

    public static Fortuna createInstance(ScheduledExecutorService scheduledExecutorService) {
        return new Fortuna(scheduledExecutorService);
    }

    private static Accumulator createAccumulator(ScheduledExecutorService scheduledExecutorService) {
        Pool[] pools = new Pool[32];
        for (int pool = 0; pool < pools.length; pool++) {
            pools[pool] = new Pool();
        }
        Accumulator accumulator = new Accumulator(pools, scheduledExecutorService);
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

    public Fortuna(ScheduledExecutorService scheduledExecutorService) {
        this(new Generator(), new RandomDataBuffer(), createAccumulator(scheduledExecutorService));
    }

    private Fortuna(Generator generator, RandomDataBuffer randomDataBuffer, Accumulator accumulator) {
        this.generator = generator;
        this.randomDataBuffer = randomDataBuffer;
        this.accumulator = accumulator;
    }

    private byte[] randomData(int bytes) {
        Instant now = Instant.now();
        Pool[] pools = accumulator.getPools();
        long millisSinceLastReseed = now.isAfter(lastReseedTime)
                ? Duration.between(lastReseedTime, now).toMillis()
                : Duration.between(now, lastReseedTime).toMillis();
        if (pools[0].size() >= MIN_POOL_SIZE && (reseedCount.get() == 0 || millisSinceLastReseed > 100)) {
            lastReseedTime = now;
            reseedCount.incrementAndGet();
            byte[] seed = new byte[pools.length * 32]; // Maximum potential length
            int seedLength = 0;
            for (int pool = 0; pool < pools.length; pool++) {
                if (reseedCount.get() % POWERS_OF_TWO[pool] == 0) {
                    System.arraycopy(pools[pool].getAndClear(), 0, seed, seedLength, 32);
                    seedLength += 32;
                }
            }
            generator.reseed(Arrays.copyOf(seed, seedLength));
        }
        if (reseedCount.get() == 0) {
            throw new IllegalStateException("Generator not reseeded yet.");
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

    public void setShutdownExecutor(boolean shutdownExecutor) {
        this.shutdownExecutor = shutdownExecutor;
    }

    @SuppressWarnings("WeakerAccess")
    public void shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        accumulator.shutdown(timeout, unit, this.shutdownExecutor);
    }

    public void shutdown() throws InterruptedException {
        shutdown(30, TimeUnit.SECONDS);
    }

}
