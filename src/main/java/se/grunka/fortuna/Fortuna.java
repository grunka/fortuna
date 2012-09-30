package se.grunka.fortuna;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Fortuna extends Random {
    private static final int MIN_POOL_SIZE = 64;
    private static final int[] POWERS_OF_TWO = new int[32];

    static {
        for (int power = 0; power < POWERS_OF_TWO.length; power++) {
            POWERS_OF_TWO[power] = (int) Math.pow(2, power);
        }
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
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
        scheduler.scheduleWithFixedDelay(new EntropySource(pools) {
            @Override
            public void collect() {
                addEvent(new byte[12]);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        //TODO wait until min_pool_size is reached (or seed file is read)
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

    static int ceil(int value, int divisor) {
        return (value / divisor) + (value % divisor == 0 ? 0 : 1);
    }

    //TODO thread safety
    //TODO seed file management

    @Override
    protected int next(int bits) {
        byte[] bytes = randomData(ceil(bits, 8));
        //TODO get bytes and put into int...
        return 0;
    }

    @Override
    public void nextBytes(byte[] bytes) {
        //TODO check if this is doable
        super.nextBytes(bytes);
    }

    @Override
    public synchronized void setSeed(long seed) {
        throw new UnsupportedOperationException("Setting the seed is not allowed");
    }
}
