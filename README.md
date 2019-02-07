# Fortuna

This is an implementation of the Fortuna PRNG, read more at [wikipedia][fortuna] or in the book [Cryptographic Engineering][ce] by Bruce Schneier.

## Usage

It's fairly simple, add the fortuna-2.0.jar to your classpath and then create a new instance using the method createInstance on the class com.grunka.random.fortuna.Fortuna. What you get is a subclass of the normal Java Random class so wherever that can be used so can this. You should re-use this instance since creating a new one takes a little while, there is no gain in re-initializing it and the instance is thread safe.

There is an included runnable class for outputting random data for testing purposes. [com.grunka.random.fortuna.Dump](https://github.com/grunka/Fortuna/blob/master/src/main/java/se/grunka/fortuna/tests/Dump.java). Dump outputs a specified number of megabytes of random data that can be used in other tools that analyze random data.

## Details

For specifics either read the book referenced above and have a look at the code. Below are some descriptions of the specific choices taken for this implementation.

### Block cipher

The block cipher used is a public domain implementation of [AES-256][aes256], in the code it uses the original name Rijndael. The reason for not using Javas own implementation is to avoid the system configuration changes needed to be allowed to use it since Java in it's default configuration only allows up to 128-bit keys.

### Entropy sources

For entropy sources I've selected several system dependant sources that are available to the Java runtime. In the current version there are [nine sources](entropy_sources). For most of the values only the two least significant bytes are used which ensures that the values are fairly unpredictable even though they are polled often.

[fortuna]: http://en.wikipedia.org/wiki/Fortuna_(PRNG)
[ce]: http://www.schneier.com/book-ce.html
[aes256]: http://en.wikipedia.org/wiki/Advanced_Encryption_Standard
[entropy_sources]: https://github.com/grunka/fortuna/tree/master/src/main/java/com/grunka/random/fortuna/entropy
