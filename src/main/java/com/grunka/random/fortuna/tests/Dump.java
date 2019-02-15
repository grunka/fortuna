package com.grunka.random.fortuna.tests;

import com.grunka.random.fortuna.Fortuna;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dump {
    //TODO decide which dump method to use, maybe combine

    private static final int MEGABYTE = 1024 * 1024;

    // Compression test: xz -e9zvkf random.data

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            usage();
            System.exit(args.length == 0 ? 0 : 1);
        }
        long megabytes = 0;
        try {
            megabytes = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            usage();
            System.err.println("Megabytes was not a number: " + args[0]);
            System.exit(1);
        }
        if (megabytes < 1) {
            usage();
            System.err.println("Needs to be at least one megabyte, was " + megabytes);
            System.exit(1);
        }
        long dataSize = megabytes * MEGABYTE;
        System.err.println("Initializing RNG...");
        Fortuna fortuna = Fortuna.createInstance();
        long start = System.currentTimeMillis();
        System.err.println("Generating data...");
        try (OutputStream output = getOutputStream(args)) {
            try (OutputStream outputStream = new BufferedOutputStream(output)) {
                byte[] buffer = new byte[1024];
                long remainingBytes = dataSize;
                while (remainingBytes > 0) {
                    fortuna.nextBytes(buffer);
                    outputStream.write(buffer);
                    remainingBytes -= buffer.length;
                    System.err.print((100 * (dataSize - remainingBytes) / dataSize) + "%\r");
                }
            }
        }
        System.err.println("Done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
        fortuna.shutdown();
    }

    private static OutputStream getOutputStream(String[] args) throws FileNotFoundException {
        if (args.length == 2) {
            return new FileOutputStream(args[1], false);
        } else {
            return System.out;
        }
    }

    private static void usage() {
        System.err.println("Usage: " + Dump.class.getName() + " <megabytes> [<file>]");
        System.err.println("Will generate <megabytes> of data and output them either to <file> or stdout if <file> is not specified");
    }

    public static void otherMain(String[] args) throws IOException, InterruptedException {
        boolean hasLimit = false;
        BigInteger limit = BigInteger.ZERO;
        if (args.length == 1) {
            String amount = args[0];
            Matcher matcher = Pattern.compile("^([1-9][0-9]*)([KMG])?$").matcher(amount);
            if (matcher.matches()) {
                String number = matcher.group(1);
                String suffix = matcher.group(2);
                limit = new BigInteger(number);
                if (suffix != null) {
                    switch (suffix) {
                        case "K":
                            limit = limit.multiply(BigInteger.valueOf(1024));
                            break;
                        case "M":
                            limit = limit.multiply(BigInteger.valueOf(1024 * 1024));
                            break;
                        case "G":
                            limit = limit.multiply(BigInteger.valueOf(1024 * 1024 * 1024));
                            break;
                        default:
                            System.err.println("Unrecognized suffix");
                            System.exit(1);
                    }
                }
                hasLimit = true;
            } else {
                System.err.println("Unrecognized amount " + amount);
                System.exit(1);
            }
        } else if (args.length > 1) {
            System.err.println("Unrecognized parameters " + Arrays.toString(args));
            System.exit(1);
        }
        Fortuna fortuna = Fortuna.createInstance();
        final BigInteger chunk = BigInteger.valueOf(4 * 1024);
        while (!hasLimit || limit.compareTo(BigInteger.ZERO) > 0) {
            final byte[] buffer;
            if (hasLimit) {
                if (chunk.compareTo(limit) < 0) {
                    buffer = new byte[chunk.intValue()];
                    limit = limit.subtract(chunk);
                } else {
                    buffer = new byte[limit.intValue()];
                    limit = BigInteger.ZERO;
                }
            } else {
                buffer = new byte[chunk.intValue()];
            }
            fortuna.nextBytes(buffer);
            System.out.write(buffer);
        }
        fortuna.shutdown();
    }
}
