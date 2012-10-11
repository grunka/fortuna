package se.grunka.fortuna;

import org.junit.Test;

public class FortunaTest {
    @Test
    public void shouldCreateInstanceAndWaitForInitialization() throws Exception {
        Fortuna fortuna = Fortuna.createInstance();
        int i = fortuna.nextInt(42);
        System.out.println("i = " + i);
    }
}
