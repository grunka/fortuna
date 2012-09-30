package se.grunka.fortuna;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import Twofish.Twofish_Algorithm;

public class Generator {
    private static final int KEY_LENGTH = 32;
    private static final int BLOCK_LENGTH = 16;
    private Counter counter;
    private final byte[] key = new byte[KEY_LENGTH];
    private final MessageDigest reseedDigest;

    public Generator() {
        counter = new Counter(128);
        try {
            reseedDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Was not able to initialize digest", e);
        }
    }

    public void reseed(byte[] seed) {
        reseedDigest.update(key);
        System.arraycopy(reseedDigest.digest(seed), 0, key, 0, KEY_LENGTH);
        counter.increment();
    }

    private byte[] generateBlocks(int blocks) {
        if (counter.isZero()) {
            throw new IllegalStateException("Generator not yet initialized");
        }
        byte[] result = new byte[blocks * BLOCK_LENGTH];
        for (int block = 0; block < blocks; block++) {
            byte[] encryptedBytes = encryptState();
            System.arraycopy(encryptedBytes, 0, result, block * BLOCK_LENGTH, BLOCK_LENGTH);
            counter.increment();
        }
        return result;
    }

    private byte[] encryptState() {
        try {
            Object sessionKey = Twofish_Algorithm.makeKey(key);
            return Twofish_Algorithm.blockEncrypt(counter.getState(), 0, sessionKey);
        } catch (InvalidKeyException e) {
            throw new Error("Unable to create key", e);
        }
    }

    public byte[] pseudoRandomData(int bytes) {
        if (bytes < 0 || bytes > 1048576) {
            throw new IllegalArgumentException("Cannot generate " + bytes + " bytes of random data");
        }
        try {
            return Arrays.copyOf(generateBlocks(Fortuna.ceil(bytes, BLOCK_LENGTH)), bytes);
        } finally {
            System.arraycopy(generateBlocks(2), 0, key, 0, KEY_LENGTH);
        }
    }
}
