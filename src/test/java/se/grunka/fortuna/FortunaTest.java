package se.grunka.fortuna;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
