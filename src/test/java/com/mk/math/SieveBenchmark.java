package com.mk.math;

import com.mk.math.PrimeLastDigit.PrimeStatistics;
import org.jooq.lambda.tuple.Tuple2;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Map;

@State(Scope.Thread)
@Warmup(iterations = 6, time = 3)
@Measurement(iterations = 5, time = 4)
@Fork(1)
public class SieveBenchmark {

    @Param({"1000000"})
    private int max;

    // stream and intStream apper to have kind of the same performance
//    @Benchmark
    public void stream(Blackhole blackhole) {
        Sieve.ofNumOfPrime(max).stream().forEach(blackhole::consume);
    }

    //    @Benchmark
    public void intStream(Blackhole blackhole) {
        Sieve.ofNumOfPrime(max).intStream().forEach(blackhole::consume);
    }

    // statistic is faster than lastDigits, consecutivePrimes1379 much slower, naturally
    //Benchmark                               (max)    Score   Error  Units
    //SieveBenchmark.primeStatistics         100000  181,913 ± 7,656  ops/s
    //SieveBenchmark.lastDigits              100000  147,601 ± 2,326  ops/s
    //SieveBenchmark.consecutivePrimes1379   100000   18,729 ± 0,315  ops/s
    //SieveBenchmark.primeStatistics        1000000   14,639 ± 0,605  ops/s
    //SieveBenchmark.lastDigits             1000000   12,519 ± 0,133  ops/s
    //SieveBenchmark.consecutivePrimes1379  1000000    1,109 ± 0,240  ops/s
    @Benchmark
    public PrimeStatistics primeStatistics() {
        return PrimeLastDigit.numPrimes(max).statistic();
    }

    @Benchmark
    public Map<Integer, Long> lastDigits() {
        return PrimeLastDigit.lastDigits(max);
    }

    @Benchmark
    public Map<Tuple2<Integer, Integer>, Long> consecutivePrimes1379() {
        return PrimeLastDigit.consecutivePrimes1379(max);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SieveBenchmark.class.getSimpleName())
                .addProfiler(StackProfiler.class)
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}