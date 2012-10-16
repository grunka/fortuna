package se.grunka.fortuna;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

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

    @Test
    public void shouldProduceRandomNumbers() throws Exception {
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
    }

    @Ignore
    @Test
    public void shouldOutputRandomData() throws Exception {
        long remainingBytes = 100L * 1024 * 1024;
        byte[] buffer = new byte[1024];
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
            long bytesPerSecond = 4L * 1024 * 1024 * 1024 / (duration / 1000);
            System.out.println("bytesPerSecond = " + bytesPerSecond);
        } finally {
            outputStream.close();
        }
    }
}
