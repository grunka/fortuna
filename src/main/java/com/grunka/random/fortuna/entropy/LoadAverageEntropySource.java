package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadAverageEntropySource implements EntropySource {

    private final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

    @Override
    public void event(EventAdder adder) {
        double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
        BigDecimal value = BigDecimal.valueOf(systemLoadAverage);
        long convertedValue = value.movePointRight(value.scale()).longValue();
        adder.add(Util.twoLeastSignificantBytes(convertedValue));
    }

    @Override
    public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
        return scheduler.scheduleWithFixedDelay(runnable, 0,1000, TimeUnit.MILLISECONDS);
    }
}
