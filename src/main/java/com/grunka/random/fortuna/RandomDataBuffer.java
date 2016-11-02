package com.grunka.random.fortuna;

import java.util.function.Supplier;

class RandomDataBuffer {
    private byte[] buffer = new byte[0];
    private int remainingBits = 0;

    synchronized int next(int bits, Supplier<byte[]> randomDataSupplier) {
        int result = 0;
        int bitsStillToTake = bits;
        while (bitsStillToTake > 0) {
            if (remainingBits == 0) {
                buffer = randomDataSupplier.get();
                remainingBits = buffer.length * 8;
                if (remainingBits <= 0) {
                    throw new IllegalStateException("Could not get more bits");
                }
            }
            int remainingBitsInByte = remainingBits % 8;
            if (remainingBitsInByte == 0) {
                remainingBitsInByte = 8;
            }
            int currentByte = buffer.length - (remainingBits / 8) - (remainingBitsInByte == 8 ? 0 : 1);
            int bitsToTakeNow = Math.min(bitsStillToTake, remainingBitsInByte);
            result = (result << bitsToTakeNow) | (((buffer[currentByte] >>> (remainingBitsInByte - bitsToTakeNow)) & 0xff) & mask(bitsToTakeNow));
            remainingBits -= bitsToTakeNow;
            bitsStillToTake -= bitsToTakeNow;
        }
        return result;
    }

    private int mask(int bits) {
        switch (bits) {
            case 1:
                return (byte) 0b1;
            case 2:
                return (byte) 0b11;
            case 3:
                return (byte) 0b111;
            case 4:
                return (byte) 0b1111;
            case 5:
                return (byte) 0b11111;
            case 6:
                return (byte) 0b111111;
            case 7:
                return (byte) 0b1111111;
            case 8:
                return (byte) 0b11111111;
            default:
                throw new IllegalArgumentException("Too many bits or no bits at all");
        }
    }
}
