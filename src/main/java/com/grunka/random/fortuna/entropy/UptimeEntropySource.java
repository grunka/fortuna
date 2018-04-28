package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import com.grunka.random.fortuna.accumulator.EventScheduler;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

public class UptimeEntropySource implements EntropySource {
    private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    @Override
    public void event(EventScheduler scheduler, EventAdder adder) {
        long uptime = runtimeMXBean.getUptime();
        adder.add(Util.twoLeastSignificantBytes(uptime));
        scheduler.schedule(1, TimeUnit.SECONDS);
    }
}
