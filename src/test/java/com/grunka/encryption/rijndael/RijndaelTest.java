package com.grunka.encryption.rijndael;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;

public class RijndaelTest {
    @Test
    public void shouldEncryptDecrypt() {
        Rijndael rijndael = new Rijndael();
        byte[] input = "hello world01234".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[input.length];
        byte[] output = new byte[encrypted.length];
        rijndael.makeKey("012345678901234567890123456789ab".getBytes(StandardCharsets.UTF_8), 256, Rijndael.DIR_ENCRYPT);
        rijndael.encrypt(input, encrypted);
        rijndael.makeKey("012345678901234567890123456789ab".getBytes(StandardCharsets.UTF_8), 256, Rijndael.DIR_DECRYPT);
        rijndael.decrypt(encrypted, output);
        assertArrayEquals(input, output);
    }
}
