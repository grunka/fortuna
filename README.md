# Fortuna

This is an implementation of the Fortuna PRNG, read more at [wikipedia][fortuna] or in the book [Cryptographic Engineering][ce] by Bruce Schneier.

## Usage

Add it as a maven dependency

```xml
<dependency>
    <groupId>com.grunka.random.fortuna</groupId>
    <artifactId>fortuna</artifactId>
    <version>2.1</version>
</dependency>
```

or clone and build (nothing more than `mvn install` is needed) and add to classpath.

Due to reasons there are a couple of ways to create an instance. Either the constructor or the methods `createInstance` on [com.grunka.random.fortuna.Fortuna][fortuna_class]. Both of these ways create a subclass of Javas own Random class so this can be used wherever that would be used. The instance created should be reused. It is thread safe, creation time is noticeable, and you do not gain anything by recreating it. Due to the background threads collecting entropy running continuouslt you should call `shutdown` on the instance if you will actually not use the instance any more.

There is an included runnable class for outputting random data for testing purposes. [com.grunka.random.fortuna.Dump][dump_class]. Dump outputs a specified number of megabytes of random data that can be used in other tools that analyze random data.

## Details

For specifics either read the book referenced above and have a look at the code. Below are some descriptions of the specific choices taken for this implementation.

### Block cipher

The block cipher used is a public domain implementation of [AES-256][aes256], in the code it uses the original name Rijndael. The reason for not using Javas own implementation is to avoid the system configuration changes needed to be allowed to use it since Java in it's default configuration only allows up to 128-bit keys.

### Entropy sources

For entropy sources I've selected several system dependant sources that are available to the Java runtime. In the current version there are [nine sources][entropy_sources]. For most of the values only the two least significant bytes are used which ensures that the values are fairly unpredictable even though they are polled often.

[fortuna]: http://en.wikipedia.org/wiki/Fortuna_(PRNG)
[ce]: http://www.schneier.com/book-ce.html
[aes256]: http://en.wikipedia.org/wiki/Advanced_Encryption_Standard
[entropy_sources]: https://github.com/grunka/fortuna/tree/master/src/main/java/com/grunka/random/fortuna/entropy
[dump_class]: https://github.com/grunka/fortuna/blob/master/src/main/java/com/grunka/random/fortuna/tests/Dump.java
[fortuna_class]: https://github.com/grunka/fortuna/blob/master/src/main/java/com/grunka/random/fortuna/Fortuna.java
