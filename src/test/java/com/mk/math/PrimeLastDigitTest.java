package com.mk.math;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

class PrimeLastDigitTest {

    @Test
    void test10() {
        // 4 primes to 10
        Map<Integer, Long> lastDigits = PrimeLastDigit.lastDigits(4);
        Map<Integer, Long> expected = Map.of(2, 1L, 3, 1L, 5, 1L, 7, 1L);
        assertSameMap(expected, lastDigits);
    }

    @Test
    void test100() {
        // 25 primes to 100
        Map<Integer, Long> lastDigits = PrimeLastDigit.lastDigits(25);
        Map<Integer, Long> expected = Map.of(1, 5L, 2, 1L, 3, 7L, 5, 1L, 7, 6L, 9, 5L);
        assertSameMap(expected, lastDigits);
    }

    @Test
    void testLast2_10() {
        Map<Tuple2<Integer, Integer>, Long> actual = PrimeLastDigit.consecutivePrimes1379(3);
        Map<Tuple2<Integer, Integer>, Long> expected = Map.of(
                tuple(7, 1), 1L,
                tuple(1, 3), 1L,
                tuple(3, 7), 1L
        );
        assertSameMap(expected, actual);
    }

    @Test
    void count5800000() {

        final PrimeLastDigit primeLastDigit = PrimeLastDigit.numPrimes(5_800_000);
        final PrimeLastDigit.PrimeStatistics sieve = primeLastDigit.statistic();
        assertAll(
                () -> assertEquals(1449824, sieve.numOf1()),
                () -> assertEquals(1450185, sieve.numOf3()),
                () -> assertEquals(1450153, sieve.numOf7()),
                () -> assertEquals(1449836, sieve.numOf9())
        );
    }

    @Test
    void count5800000_2() {
        final Map<Integer, Long> lastDigits = PrimeLastDigit.lastDigits(5_800_000);
        Map<Integer, Long> expected = Map.of(
                1, 1449824L,
                2, 1L,
                3, 1450185L,
                5, 1L,
                7, 1450153L,
                9, 1449836L);
        assertSameMap(expected, lastDigits);
    }

    @Test
    void countMyriadMyriad() {
        final PrimeLastDigit primeLastDigit = PrimeLastDigit.numCouples(100_000_000);
        final PrimeLastDigit.PrimeStatistics sieve = primeLastDigit.statistic();
        assertAll(
                () -> assertEquals(4623042, sieve.numOf11(), "1,1"),
                () -> assertEquals(7429438, sieve.numOf13(), "1,3"),
                () -> assertEquals(7504612, sieve.numOf17(), "1,7"),
                () -> assertEquals(5442345, sieve.numOf19(), "1,9"),

                () -> assertEquals(6010982, sieve.numOf31(), "3,1"),
                () -> assertEquals(4442562, sieve.numOf33(), "3,3"),
                () -> assertEquals(7043695, sieve.numOf37(), "3,7"),
                () -> assertEquals(7502896, sieve.numOf39(), "3,9"),

                () -> assertEquals(6373981, sieve.numOf71(), "7,1"),
                () -> assertEquals(6755195, sieve.numOf73(), "7,3"),
                () -> assertEquals(4439355, sieve.numOf77(), "7,7"),
                () -> assertEquals(7431870, sieve.numOf79(), "7,9"),

                () -> assertEquals(7991431, sieve.numOf91(), "9,1"),
                () -> assertEquals(6372941, sieve.numOf93(), "9,3"),
                () -> assertEquals(6012739, sieve.numOf97(), "9,7"),
                () -> assertEquals(4622916, sieve.numOf99(), "9,9")
        );
    }

    @Test
    void countMyriadMyriad_2() {
        final Map<Tuple2<Integer, Integer>, Long> actual = PrimeLastDigit.consecutivePrimes1379(100_000_000);
        Map<Tuple2<Integer, Integer>, Long> expected = Map.ofEntries(
                entry(tuple(1, 1), 4623042L),
                entry(tuple(1, 3), 7429438L),
                entry(tuple(1, 7), 7504612L),
                entry(tuple(1, 9), 5442345L),
                entry(tuple(3, 1), 6010982L),
                entry(tuple(3, 3), 4442562L),
                entry(tuple(3, 7), 7043695L),
                entry(tuple(3, 9), 7502896L),
                entry(tuple(7, 1), 6373981L),
                entry(tuple(7, 3), 6755195L),
                entry(tuple(7, 7), 4439355L),
                entry(tuple(7, 9), 7431870L),
                entry(tuple(9, 1), 7991431L),
                entry(tuple(9, 3), 6372941L),
                entry(tuple(9, 7), 6012739L),
                entry(tuple(9, 9), 4622916L)
        );

        assertSameMap(expected, actual);
    }

    static <K, V> void assertSameMap(final Map<K, V> expected, final Map<K, V> actual) {
        assertSameMap(expected, actual, "");
    }

    static <K, V> void assertSameMap(final Map<K, V> expected, final Map<K, V> actual, final String message) {
        final MapDifference<K, V> difference = Maps.difference(expected, actual);
        if (!difference.areEqual()) {
            final String newLine = System.lineSeparator();
            final StringBuilder builder = new StringBuilder(message).append(newLine);
            difference.entriesOnlyOnLeft().forEach((k, v) ->
                    builder.append(String.format("Missing    entry %s -> %s", k, v)).append(newLine));
            difference.entriesOnlyOnRight().forEach((k, v) ->
                    builder.append(String.format("Unexpected entry %s -> %s", k, v)).append(newLine));
            difference.entriesDiffering().forEach((k, diff) ->
                    builder.append(String.format("Key %s expected %s got %s", k, diff.leftValue(), diff.rightValue())).append(newLine));
            fail(builder.toString());
        }
    }
}