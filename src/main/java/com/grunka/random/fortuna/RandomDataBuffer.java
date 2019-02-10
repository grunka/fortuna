package com.grunka.random.fortuna;

import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

class RandomDataBuffer {

    private static final Logger LOG = Logger.getLogger(RandomDataBuffer.class.getName());
    private static final int RANDOM_DATA_CHUNK_SIZE = 1024 * 100;

    private ByteBuffer currentBuffer;
    private ByteBuffer exhaustedBuffer;
    private BlockingQueue<ByteBuffer> bufferQueue = new ArrayBlockingQueue<>(3);

    private final Function<ByteBuffer, ByteBuffer> randomDataSupplier;
    private final Future<?> producerFuture;
    private boolean producing = true;

    private int remainingBits = 0;

    RandomDataBuffer(final ScheduledExecutorService scheduledExecutorService, final Function<ByteBuffer, ByteBuffer> randomDataSupplier) {
        this.randomDataSupplier = randomDataSupplier;
        producerFuture = scheduledExecutorService.submit(() -> {
            while (producing) {
                try {
                    ByteBuffer bufferToFill = exhaustedBuffer == null
                        ? createNewBuffer()
                        : exhaustedBuffer;
                    bufferQueue.put(randomDataSupplier.apply(bufferToFill));
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "Problem putting filled buffer into queue.");
                }
            }
        });
        waitForQueueInitialization();
    }

    private void waitForQueueInitialization() {
        while (bufferQueue.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new Error("Interrupted while waiting for initialization", e);
            }
        }
    }

    synchronized int next(int bits) {
        int result = 0;
        int bitsStillToTake = bits;
        while (bitsStillToTake > 0) {
            if (remainingBits == 0) {
                currentBuffer = nextBuffer(currentBuffer);
                remainingBits = currentBuffer.limit() * 8;
                if (remainingBits <= 0) {
                    throw new IllegalStateException("Could not get more bits");
                }
            }
            int remainingBitsInByte = remainingBits % 8;
            if (remainingBitsInByte == 0) {
                remainingBitsInByte = 8;
            }
            int currentByte = currentBuffer.limit() - (remainingBits / 8) - (remainingBitsInByte == 8 ? 0 : 1);
            int bitsToTakeNow = Math.min(bitsStillToTake, remainingBitsInByte);
            result = (result << bitsToTakeNow) | (((currentBuffer.get(currentByte) >>> (remainingBitsInByte - bitsToTakeNow)) & 0xff) & mask(bitsToTakeNow));
            remainingBits -= bitsToTakeNow;
            bitsStillToTake -= bitsToTakeNow;
        }
        return result;
    }

    private ByteBuffer nextBuffer(ByteBuffer previous) {
        this.exhaustedBuffer = previous;
        if (this.exhaustedBuffer == null) {
            this.exhaustedBuffer = createNewBuffer();
        }
        if (this.bufferQueue.isEmpty()) {
            return randomDataSupplier.apply(exhaustedBuffer);
        } else {
            return bufferQueue.poll();
        }
    }

    void stopProducing() {
        this.producing = false;
        this.producerFuture.cancel(true);
    }

    private static ByteBuffer createNewBuffer() {
        return ByteBuffer.allocateDirect(RANDOM_DATA_CHUNK_SIZE);
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
