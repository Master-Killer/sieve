package com.mk.math;

import org.jooq.lambda.Seq;

import java.util.Comparator;
import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Sieve extends Spliterators.AbstractIntSpliterator {

    private static final boolean UNCHECKED = false;
    private static final boolean COMPOSITE = true;
    private static final int OVERFLOW = 46341;

    private final boolean[] primes;
    private final int targetNumberOfPrimes;
    private final int max;
    private final int firstNonTrivial;

    private int numberOfPrimes;
    private int i;

    private Sieve(int num) {
        super(num, ORDERED | DISTINCT | SORTED | NONNULL | IMMUTABLE | CONCURRENT | SIZED);
        // https://math.stackexchange.com/questions/1348247/inverse-of-prime-counting-function
        // 𝑛(ln𝑛+lnln𝑛−1)≤𝑥≤(𝑛+1)(ln(𝑛+1)+lnln(𝑛+1))

        this.targetNumberOfPrimes = num;
        this.max = (int) Math.ceil((num + 1) * (Math.log(num + 1) + Math.log(Math.log(num + 1)))) + 1;

        firstNonTrivial = 5;
        // array of non 2, 3-multiples numbers from firstNonTrivial to max
        final int size = arraySize();
        this.primes = new boolean[size];

        i = -3; // skip the first two and prepare for a pre-increment
    }

    private int arraySize() {
        // from firstNonTrivial to max included for odd number
        // size is 1-based and index 0-based
        return index(this.max) + 1;
    }

    /**
     * return index of number in the array of primes
     */
    private int index(int number) {
        // n   n-5  %/2 (%+1)/3   index  i/2  i+i/2 i%2  i*3  i*3-i%2
        // 5  ( 0) ( 0)   (0)   ->  0 ->  0     0    0     0      0
        // 7  ( 2) ( 1)   (0)   ->  1 ->  0     1    1     3      2
        // 11 ( 6) ( 3)   (1)   ->  2 ->  1     3    0     6      6
        // 13 ( 8) ( 4)   (1)   ->  3 ->  1     4    1     9      8
        // 17 (12) ( 6)   (2)   ->  4 ->  2     6    0    12     12
        // 19 (14) ( 7)   (2)   ->  5 ->  2     7    1    15     14
        // 23 (18) ( 9)   (3)   ->  6 ->  3     9    0    18     18
        // 25 (20) (10)   (3)   ->  7 ->  3    10    1    21     20
        // 29 (24) (12)   (4)   ->  8 ->  4    12    0    24     24
        // 31 (26) (13)   (4)   ->  9 ->  4    13    1    27     26
        // 35 (30) (15)   (5)   -> 10 ->  5    15    0    30     30

        final int indexOfOdds = (number - firstNonTrivial) / 2;
        int gap = (indexOfOdds + 1) / 3;
        return indexOfOdds - gap;
    }

    private int num(int index) {
        return (index * 3) - (index % 2) + firstNonTrivial;
    }

    public static Sieve ofNumOfPrime(int numOfPrime) {
        return new Sieve(numOfPrime);
    }

    public static Sieve ofNumOfCouples(int numOfPrime) {
        // for couples, discard (2, 3), (3, 5), (5, 7) and need n+1 prime for n couples
        return new Sieve(numOfPrime + 4);
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        if (i == -3) {
            i++;
            numberOfPrimes++;
            action.accept(2);
            return numberOfPrimes < targetNumberOfPrimes;
        }

        if (i == -2) {
            i++;
            numberOfPrimes++;
            action.accept(3);
            return numberOfPrimes < targetNumberOfPrimes;
        }

        while (numberOfPrimes < targetNumberOfPrimes) {
            if (primes[++i] == UNCHECKED) {
                // if unchecked then it is the next prime.
                // no need to mark as prime because the array is too tight
                // for the chosen bounds to contain any unchecked and composite number at the end of the algorithm.
                // (array would need to be of size >=highestPrime*highestPrime for this to happen)
                final int prime = num(i);
                numberOfPrimes++;
                action.accept(prime);
                if (prime < OVERFLOW) {
                    for (int j = prime * prime; j < max; j += prime) {
                        if (j % 2 == 0 || j % 3 == 0) continue;
                        // mark multiples as composite
                        primes[index(j)] = COMPOSITE;
                    }
                }
                return numberOfPrimes < targetNumberOfPrimes;
            }
        }

        return false;
    }

    public Stream<Integer> stream() {
        return StreamSupport.stream(this, false);
    }

    public Seq<Integer> seq() {
        return Seq.seq(stream());
    }

    public IntStream intStream() {
        return StreamSupport.intStream(this, false);
    }

    @Override
    public Comparator<? super Integer> getComparator() {
        return null; // natural order
    }
}
