package se.grunka.fortuna;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import se.grunka.fortuna.accumulator.Accumulator;
import se.grunka.fortuna.entropy.FreeMemoryEntropySource;
import se.grunka.fortuna.entropy.GarbageCollectorEntropySource;
import se.grunka.fortuna.entropy.LoadAverageEntropySource;
import se.grunka.fortuna.entropy.SchedulingEntropySource;
import se.grunka.fortuna.entropy.ThreadTimeEntropySource;
import se.grunka.fortuna.entropy.URandomEntropySource;
import se.grunka.fortuna.entropy.UptimeEntropySource;

public class Fortuna extends Random {
    private static final int MIN_POOL_SIZE = 64;
    private static final int[] POWERS_OF_TWO = initializePowersOfTwo();

    private static int[] initializePowersOfTwo() {
        int[] result = new int[32];
        for (int power = 0; power < result.length; power++) {
            result[power] = (int) Math.pow(2, power);
        }
        return result;
    }

    private long lastReseedTime = 0;
    private long reseedCount = 0;
    private final Generator generator;
    private final Pool[] pools;

    public static Fortuna createInstance() {
        Pool[] pools = new Pool[32];
        for (int pool = 0; pool < pools.length; pool++) {
            pools[pool] = new Pool();
        }
        Accumulator accumulator = new Accumulator(pools);
        //TODO ADD ALL THE SOURCES
        accumulator.addSource(new SchedulingEntropySource());
        accumulator.addSource(new GarbageCollectorEntropySource());
        accumulator.addSource(new LoadAverageEntropySource());
        accumulator.addSource(new FreeMemoryEntropySource());
        accumulator.addSource(new ThreadTimeEntropySource());
        accumulator.addSource(new UptimeEntropySource());
        if (Files.exists(Paths.get("/dev/urandom"))) {
            accumulator.addSource(new URandomEntropySource());
        }
        //TODO ... or wait for seed file to be used
        while (pools[0].size() < MIN_POOL_SIZE) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new Error("Interrupted while waiting for initialization", e);
            }
        }
        return new Fortuna(new Generator(), pools);
    }

    private Fortuna(Generator generator, Pool[] pools) {
        this.generator = generator;
        this.pools = pools;
    }

    private byte[] randomData(int bytes) {
        long now = System.currentTimeMillis();
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

    //TODO thread safety
    //TODO seed file management

    @Override
    protected int next(int bits) {
        byte[] bytes = randomData(Util.ceil(bits, 8));
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result |= bytes[i] << (8 * i);
        }
        return result >>> (bytes.length * 8 - bits);
    }

    @Override
    public synchronized void setSeed(long seed) {
        // Does not do anything
    }

}
