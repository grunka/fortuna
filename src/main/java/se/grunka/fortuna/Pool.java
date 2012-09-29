package se.grunka.fortuna;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Pool {
    private final MessageDigest poolDigest;
    private int size = 0;

    public Pool() {
        try {
            poolDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Could not initialize digest", e);
        }
    }

    public int size() {
        return size;
    }

    public void add(int source, byte[] event) {
        if (source < 0 || source > 255) {
            throw new IllegalArgumentException("Source needs to be in the range 0 to 255, it was " + source);
        }
        if (event.length < 1 || event.length > 32) {
            throw new IllegalArgumentException("The length of the event need to be in the range 1 to 32, it was " + event.length);
        }
        size += event.length + 2;
        poolDigest.update(new byte[]{(byte) source, (byte) event.length});
        poolDigest.update(event);
    }

    public byte[] getAndClear() {
        size = 0;
        return poolDigest.digest();
    }
}
