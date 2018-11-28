package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class URandomEntropySource implements EntropySource {

    private static final Logger LOGGER = Logger.getLogger(URandomEntropySource.class.getName());
    private static final String DEV_URANDOM = "/dev/urandom";

    private final byte[] bytes = new byte[32];

    @Override
    public void event(EventAdder adder) {
        try {
            try (FileInputStream inputStream = new FileInputStream(DEV_URANDOM)) {
                int bytesRead = inputStream.read(bytes);
                assert bytesRead == bytes.length;
                adder.add(bytes);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Cannot read random bytes from "+DEV_URANDOM, e);
        }
    }

    @Override
    public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
        return scheduler.scheduleWithFixedDelay(runnable,0, 100, TimeUnit.MILLISECONDS);
    }

}
