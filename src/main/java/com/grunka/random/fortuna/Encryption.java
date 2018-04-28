package com.grunka.random.fortuna;

import com.grunka.encryption.rijndael.Rijndael;

class Encryption {
    private final Rijndael rijndael = new Rijndael();

    void setKey(byte[] key) {
        rijndael.makeKey(key, key.length * 8, Rijndael.DIR_ENCRYPT);
    }

    byte[] encrypt(byte[] data) {
        byte[] result = new byte[data.length];
        rijndael.encrypt(data, result);
        return result;
    }
}
