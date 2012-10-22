package se.grunka.fortuna;

import java.security.InvalidKeyException;

import Twofish.Twofish_Algorithm;

public class Encryption {
    private Object sessionKey;

    public void setKey(byte[] key) {
        try {
            sessionKey = Twofish_Algorithm.makeKey(key);
        } catch (InvalidKeyException e) {
            throw new Error("Unable to create key", e);
        }
    }

    public byte[] encrypt(byte[] data) {
        return Twofish_Algorithm.blockEncrypt(data, 0, sessionKey);
    }
}
