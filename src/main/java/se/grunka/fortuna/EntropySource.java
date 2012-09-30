package se.grunka.fortuna;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class EntropySource implements Runnable {

    private static AtomicInteger sourceCount = new AtomicInteger(0);
    private int pool = 0;
    private final int source = sourceCount.getAndIncrement();
    private final Pool[] pools;

    public EntropySource(Pool[] pools) {
        this.pools = pools;
    }

    public final void addEvent(byte[] event) {
        pools[pool].add(source, event);
        pool = (pool + 1) % pools.length;
    }

    abstract public void collect();

    @Override
    public void run() {
        collect();
    }
}
