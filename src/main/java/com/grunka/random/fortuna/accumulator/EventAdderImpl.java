package com.grunka.random.fortuna.accumulator;

import com.grunka.random.fortuna.Pool;

import java.util.logging.Level;
import java.util.logging.Logger;

class EventAdderImpl implements EventAdder {

    private static final Logger LOGGER = Logger.getLogger(EventAdderImpl.class.getName());

    private int pool;
    private final int sourceId;
    private final Pool[] pools;
    private Class<? extends EntropySource> entropySourceClass;

    EventAdderImpl(int sourceId, Pool[] pools, Class<? extends EntropySource> entropySourceClass) {
        this.sourceId = sourceId;
        this.pools = pools;
        this.entropySourceClass = entropySourceClass;
        pool = 0;
    }

    @Override
    public void add(byte[] event) {
        pool = (pool + 1) % pools.length;
        pools[pool].add(sourceId, event);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Added {0} bytes entropy to pool {1} from {2} using thread {3}.", new Object[]{event.length, pool, entropySourceClass.getSimpleName(), Thread.currentThread().getName()});
        }
    }
}
