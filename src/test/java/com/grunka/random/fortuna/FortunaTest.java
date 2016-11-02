package com.grunka.random.fortuna;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FortunaTest {
    @Test
    public void shouldCreateInstanceAndWaitForInitialization() throws Exception {
        Fortuna fortuna = Fortuna.createInstance();
        try {
            fortuna.nextInt(42);
        } catch (IllegalStateException ignored) {
            fail("Did not wait for initialization");
        }
    }

    @Ignore
    @Test
    public void shouldProduceEvenDistribution() throws Exception {
        int[] numbers = new int[10];
        Fortuna fortuna = Fortuna.createInstance();
        for (int i = 0; i < 1000000; i++) {
            numbers[fortuna.nextInt(10)]++;
        }
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int number : numbers) {
            if (number > highest) {
                highest = number;
            }
            if (number < lowest) {
                lowest = number;
            }
        }
        System.out.println("numbers = " + Arrays.toString(numbers));
        int percentage = (100 * (highest - lowest)) / lowest;
        System.out.println("percentage = " + percentage);
        assertEquals(0, percentage);
    }

    @Ignore
    @Test
    public void dumpData() throws Exception {
        dumpData(Fortuna.createInstance(), Paths.get("random.fortuna.out"));
    }

    private void dumpData(Random random, Path path) throws IOException {
        Files.deleteIfExists(path);
        Files.createFile(path);
        System.out.println("path.toRealPath() = " + path.toRealPath());
        byte[] fourK = new byte[4 * 1024];
        for (int i = 0; i < 2 * 1024 * 1024; i++) {
            random.nextBytes(fourK);
            Files.write(path, fourK, StandardOpenOption.APPEND);
        }
    }
}
