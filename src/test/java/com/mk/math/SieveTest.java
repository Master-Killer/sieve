package com.mk.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SieveTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void intStream(int numPrime) {
        List<Integer> primes = List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29);

        // verify size
        assertAll(
                () -> assertEquals(numPrime, Sieve.ofNumOfPrime(numPrime).getExactSizeIfKnown()),
                () -> assertEquals(numPrime, Sieve.ofNumOfPrime(numPrime).estimateSize()),
                () -> assertEquals(numPrime, Sieve.ofNumOfPrime(numPrime).stream().count()));

        assertAll(Sieve.ofNumOfPrime(numPrime).seq().zipWithIndex()
                .map(t -> () -> {
                    final int index = Math.toIntExact(t.v2);
                    final Integer expectedPrime = primes.get(index);
                    final Integer actual = t.v1;
                    assertEquals(expectedPrime, actual, (String.format("Prime #%d ", index + 1)));
                }));
    }

    @Test
    void shouldBeSameAnswerAsBigInteger_ofNum() {
        final Sieve sieve = Sieve.ofNumOfPrime(100_000);
        assertAll(sieve.stream()
                .map(prime -> () -> {
                    boolean probablePrime = BigInteger.valueOf(prime).isProbablePrime(100);
                    assertTrue(probablePrime, String.format("Expected %d to be composite, but was prime", prime));
                }));
    }
}