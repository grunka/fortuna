package se.grunka.fortuna;

public class Counter {
    private final byte[] state = new byte[16];

    public void increment() {
        int position = 0;
        byte newValue;
        do {
            newValue = ++state[position];
            position = (position + 1) % state.length;
        }
        while (newValue == 0);
    }

    public byte[] getState() {
        return state;
    }

    public boolean isZero() {
        for (byte b : state) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }
}
