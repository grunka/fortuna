package se.grunka.fortuna;

import java.util.Random;

public class Fortuna extends Random {
    private static final int MIN_POOL_SIZE = 64;

    private long lastReseedTime = 0;
    private long reseedCount = 0;
    private final Generator generator;
    private final Pool[] pools;

    public static Fortuna createInstance() {
        Pool[] pools = new Pool[32];
        for (int pool = 0; pool < pools.length; pool++) {
            pools[pool] = new Pool();
        }
        return new Fortuna(new Generator(), pools);
    }

    private Fortuna(Generator generator, Pool[] pools) {
        this.generator = generator;
        this.pools = pools;
    }

    private byte[] randomData(int bytes) {
        if (pools[0].size() >= MIN_POOL_SIZE && System.currentTimeMillis() - lastReseedTime > 100) {
            reseedCount++;
            byte[] seed = new byte[0];
            for (int pool = 0; pool < pools.length; pool++) {
                //TODO all this...
            }
            generator.reseed(seed);
        }
        if (reseedCount == 0) {
            throw new IllegalStateException("Generator not reseeded yet");
        } else {
            return generator.pseudoRandomData(bytes);
        }
    }

    //TODO seed file management

    @Override
    protected int next(int bits) {
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
