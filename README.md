# Fortuna

This is an implementation of the Fortuna PRNG, read more at [wikipedia](http://en.wikipedia.org/wiki/Fortuna_(PRNG)) or in the book [Cryptographic Engineering](http://www.schneier.com/book-ce.html) by Bruce Schneier.

## Usage

It's fairly simple, add the fortuna-1.0.jar to your classpath and then create a new instance using the method createInstance on the class se.grunka.fortuna.Fortuna. What you get is a subclass of the normal Java Random class so wherever that can be used so can this. You should re-use this instance since creating a new one takes a little while, there is no gain in re-initializing it and the instance is thread safe.

There are two included runnable classes for outputting random data for testing purposes. [se.grunka.fortuna.tests.Image](https://github.com/grunka/Fortuna/blob/master/src/main/java/se/grunka/fortuna/tests/Image.java) and [se.grunka.fortuna.Dump](https://github.com/grunka/Fortuna/blob/master/src/main/java/se/grunka/fortuna/tests/Dump.java). Image outputs and image of specified size where each pixel is either black or white depending on a coin-flip. Dump outputs a specified number of megabytes of random data that can be used in other tools that analyze random data.

## Details

For specifics either read the book referenced above and have a look at the code. Below are some descriptions of the specific choices taken for this implementation.

### Block cipher

The block cipher used is a public domain implementation of [AES-256](http://en.wikipedia.org/wiki/Advanced_Encryption_Standard), in the code it uses the original name Rijndael. The reason for not using Javas own implementation is to avoid the system configuration changes needed to be allowed to use it since Java in it's default configuration only allows up to 128-bit keys.

### Entropy sources

For entropy sources I've selected several system dependant sources that are available to the Java runtime. In the current version there are [seven sources](https://github.com/grunka/Fortuna/tree/master/src/main/java/se/grunka/fortuna/entropy). For most of the values only the two least significant bytes are used which ensures that the values are fairly unpredictable even though they are polled often.

## Donations

If you would like to support development like this from me click the link below and do the PayPal thing with any amount you see fit.

[Donate](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=S5LTB8U3LVPSQ&lc=SE&item_name=Grunka%2ese&currency_code=SEK&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)
