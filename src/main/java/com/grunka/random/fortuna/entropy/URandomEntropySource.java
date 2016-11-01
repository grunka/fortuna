package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import com.grunka.random.fortuna.accumulator.EventScheduler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class URandomEntropySource implements EntropySource {

    private final byte[] bytes = new byte[32];

    @Override
    public void event(EventScheduler scheduler, EventAdder adder) {
        try {
            try (FileInputStream inputStream = new FileInputStream("/dev/urandom")) {
                int bytesRead = inputStream.read(bytes);
                assert bytesRead == bytes.length;
                adder.add(bytes);
                scheduler.schedule(100, TimeUnit.MILLISECONDS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
