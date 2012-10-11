package se.grunka.fortuna;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Accumulator {
    private final Map<Integer, EventContext> eventContexts = new ConcurrentHashMap<Integer, EventContext>();
    private final AtomicInteger sourceCount = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        private final ThreadFactory delegate = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = delegate.newThread(r);
            thread.setDaemon(true);
            return thread;
        }
    });
    private final Pool[] pools;

    public Accumulator(Pool[] pools) {
        this.pools = pools;
    }

    public void addSource(EntropySource entropySource) {
        final int sourceId = sourceCount.incrementAndGet();
        final EventAdder eventAdder = new EventAdder() {
            private int pool = 0;

            @Override
            public void add(byte[] event) {
                pool = (pool + 1) % pools.length;
                pools[pool].add(sourceId, event);
            }
        };
        EventScheduler eventScheduler = new EventScheduler() {
            private final AtomicBoolean scheduled = new AtomicBoolean(false);
            @Override
            public void schedule(long delay, TimeUnit timeUnit) {
                scheduled.set(true);
                scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        EventContext eventContext = eventContexts.get(sourceId);
                        scheduled.set(false);
                        eventContext.source.event(eventContext.scheduler, eventContext.adder);
                        if (!scheduled.get()) {
                            scheduler.schedule(this, 0, TimeUnit.MILLISECONDS);
                        }
                    }
                }, delay, timeUnit);
            }
        };
        EventContext eventContext = new EventContext(entropySource, eventAdder, eventScheduler);
        eventContexts.put(sourceId, eventContext);
        eventScheduler.schedule(0, TimeUnit.MILLISECONDS);
    }

    private static class EventContext {
        public final EntropySource source;
        public final EventAdder adder;
        public final EventScheduler scheduler;

        private EventContext(EntropySource source, EventAdder adder, EventScheduler scheduler) {
            this.source = source;
            this.adder = adder;
            this.scheduler = scheduler;
        }
    }
}
