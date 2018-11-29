package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BufferPoolEntropySource implements EntropySource {

    @Override
    public void event(EventAdder adder) {
        long sum = 0;
        List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean bufferPoolMXBean : bufferPoolMXBeans) {
            sum += bufferPoolMXBean.getMemoryUsed();
        }
        if (sum > 0) {
            adder.add(Util.twoLeastSignificantBytes(sum + System.currentTimeMillis()));
        }
    }

    @Override
    public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
        return scheduler.scheduleWithFixedDelay(runnable, 0, 5, TimeUnit.SECONDS);
    }

}
