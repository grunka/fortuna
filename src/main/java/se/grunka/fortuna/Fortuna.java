package se.grunka.fortuna;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fortuna extends Random {
    public static Fortuna createInstance() {
        Fortuna fortuna = new Fortuna();
        fortuna.initialize();
        return fortuna;
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    Fortuna() {
    }

    private void initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                executorService.shutdown();
            }
        });
    }

    @Override
    protected int next(int bits) {
        return 0;
    }

    @Override
    public synchronized void setSeed(long seed) {
        throw new UnsupportedOperationException("Setting the seed is not allowed");
    }
}
