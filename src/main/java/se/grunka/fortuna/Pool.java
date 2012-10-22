package se.grunka.fortuna;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Pool {
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger size = new AtomicInteger(0);
    private final MessageDigest poolDigest = createDigest();

    private MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Could not initialize digest", e);
        }
    }

    public int size() {
        return size.get();
    }

    public void add(int source, byte[] event) {
        lock.lock();
        try {
            if (source < 0 || source > 255) {
                throw new IllegalArgumentException("Source needs to be in the range 0 to 255, it was " + source);
            }
            if (event.length < 1 || event.length > 32) {
                throw new IllegalArgumentException("The length of the event need to be in the range 1 to 32, it was " + event.length);
            }
            size.addAndGet(event.length + 2);
            poolDigest.update(new byte[]{(byte) source, (byte) event.length});
            poolDigest.update(event);
        } finally {
            lock.unlock();
        }
    }

    public byte[] getAndClear() {
        lock.lock();
        try {
            size.set(0);
            return poolDigest.digest();
        } finally {
            lock.unlock();
        }
    }
}
