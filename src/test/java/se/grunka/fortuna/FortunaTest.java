package se.grunka.fortuna;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

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
    public void shouldOutputRandomData() throws Exception {
        // Compression test: xz -e9zvkf random.data
        long dataSize = 100L * 1024 * 1024;
        long remainingBytes = dataSize;
        byte[] buffer = new byte[1024 * 1024];
        Fortuna fortuna = Fortuna.createInstance();
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("random.data", false));
        try {
            long before = System.currentTimeMillis();
            while (remainingBytes > 0) {
                fortuna.nextBytes(buffer);
                outputStream.write(buffer);
                remainingBytes -= buffer.length;
            }
            long after = System.currentTimeMillis();
            long duration = after - before;
            long bytesPerSecond = dataSize / (duration / 1000);
            System.out.println("bytesPerSecond = " + bytesPerSecond);
        } finally {
            outputStream.close();
        }
    }
}
