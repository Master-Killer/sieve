package com.mk.math;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jooq.lambda.tuple.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.jooq.lambda.tuple.Tuple.tuple;

public class PrimeLastDigit {

    Sieve sieve;

    private PrimeLastDigit(Sieve sieve) {
        this.sieve = sieve;
    }

    public static PrimeLastDigit numCouples(int numOfCouples) {
        return new PrimeLastDigit(Sieve.ofNumOfCouples(numOfCouples));
    }

    public static PrimeLastDigit numPrimes(int numOfPrime) {
        return new PrimeLastDigit(Sieve.ofNumOfPrime(numOfPrime));
    }

    @Getter
    @Accessors(fluent = true)
    public static class PrimeStatistics implements IntConsumer {

        private int previous;

        private int highestPrime;

        private int numOf1;
        private int numOf3;
        private int numOf7;
        private int numOf9;

        private int numOf11;
        private int numOf13;
        private int numOf17;
        private int numOf19;

        private int numOf31;
        private int numOf33;
        private int numOf37;
        private int numOf39;

        private int numOf71;
        private int numOf73;
        private int numOf77;
        private int numOf79;

        private int numOf91;
        private int numOf93;
        private int numOf97;
        private int numOf99;

        @Override
        public void accept(int value) {
            if (value > highestPrime) highestPrime = value;
            int c = value % 10;
            int p = previous % 10;
            switch (c) {
                case 1 -> {
                    numOf1++;
                    switch (p) {
                        case 1 -> numOf11++;
                        case 3 -> numOf31++;
                        case 7 -> numOf71++;
                        case 9 -> numOf91++;
                    }
                }
                case 3 -> {
                    numOf3++;
                    switch (p) {
                        case 1 -> numOf13++;
                        case 3 -> numOf33++;
                        case 7 -> numOf73++;
                        case 9 -> numOf93++;
                    }
                }
                case 7 -> {
                    numOf7++;
                    switch (p) {
                        case 1 -> numOf17++;
                        case 3 -> numOf37++;
                        case 7 -> numOf77++;
                        case 9 -> numOf97++;
                    }
                }
                case 9 -> {
                    numOf9++;
                    switch (p) {
                        case 1 -> numOf19++;
                        case 3 -> numOf39++;
                        case 7 -> numOf79++;
                        case 9 -> numOf99++;
                    }
                }
            }
            previous = value;
        }
    }

    public PrimeStatistics statistic() {
        final PrimeStatistics primeStatistics = new PrimeStatistics();
        this.sieve.intStream().forEach(primeStatistics);
        return primeStatistics;
    }

    public static Map<Integer, Long> lastDigits(int numPrime) {
        return Sieve.ofNumOfPrime(numPrime).stream()
                .collect(groupingBy(p -> p % 10, counting()));
    }

    /**
     * Last digits of couples beyond (5,7)
     */
    public static Map<Tuple2<Integer, Integer>, Long> consecutivePrimes1379(int numCouples) {
        return Sieve.ofNumOfCouples(numCouples).seq()
                .filter(p -> p > 5)
                .map(p -> p % 10)
                .sliding(2)
                .map(s -> {
                    List<Integer> integers = s.toList();
                    return tuple(integers.get(0), integers.get(1));
                })
                .groupBy(Function.identity(), counting());
    }
}
