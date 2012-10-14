package se.grunka.fortuna.entropy;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.TimeUnit;

import se.grunka.fortuna.EntropySource;
import se.grunka.fortuna.EventAdder;
import se.grunka.fortuna.EventScheduler;
import se.grunka.fortuna.Util;

public class LoadAverageEntropySource implements EntropySource {

    private final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

    @Override
    public void event(EventScheduler scheduler, EventAdder adder) {
        double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
        adder.add(Util.twoLeastSignificantBytes(Double.doubleToLongBits(systemLoadAverage)));
        scheduler.schedule(1000, TimeUnit.MILLISECONDS);
    }
}
