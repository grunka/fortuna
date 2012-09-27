package se.grunka.fortuna;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import Twofish.Twofish_Algorithm;
import org.junit.Before;
import org.junit.Test;

public class GeneratorTest {

    private Generator generator;

    @Before
    public void before() throws Exception {
        generator = new Generator();
    }

    @Test
    public void shouldDoThings() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest("trolololo".getBytes("UTF-8"));
        System.out.println("bytes.length = " + bytes.length);
    }

    @Test
    public void shouldTestEncryption() throws Exception {
        byte[] result;
        byte[] key = new byte[32];
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            result = cipher.doFinal(new Counter(128).getState());
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Could not find algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new Error("Could not find padding", e);
        } catch (InvalidKeyException e) {
            throw new Error("Could not create key", e);
        } catch (IllegalBlockSizeException e) {
            throw new Error("Illegal block size", e);
        } catch (BadPaddingException e) {
            throw new Error("Bad padding", e);
        }
        System.out.println("Arrays.toString(result) = " + Arrays.toString(result));
    }

    @Test
    public void shouldTestAnotherEncryption() throws Exception {
        Object key = Twofish_Algorithm.makeKey(new byte[32]);
        byte[] result = Twofish_Algorithm.blockEncrypt(new Counter(128).getState(), 0, key);
        System.out.println("Arrays.toString(result) = " + Arrays.toString(result));
    }

    @Test
    public void shouldTestCopying() throws Exception {
        int blocks = 5;
        byte[] result = new byte[blocks * 3];
        for (int block = 0; block < blocks; block++) {
            System.arraycopy(new byte[]{1, 1, 1}, 0, result, block * 3, 3);
        }
        System.out.println("Arrays.toString(result) = " + Arrays.toString(result));
    }
}
