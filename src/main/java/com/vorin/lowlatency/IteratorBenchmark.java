package com.vorin.lowlatency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 *
 * @author Adam
 */
@SuppressWarnings("checkstyle:all")
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 7, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(4)
@State(Scope.Benchmark)
public class IteratorBenchmark {
    /*
# Run complete. Total time: 00:04:26
Benchmark                     Mode  Cnt     Score    Error  Units
IteratorBenchmark.classicFor  avgt   28  1671.269 ± 40.064  us/op
IteratorBenchmark.forLoop     avgt   28  1727.569 ± 18.476  us/op
IteratorBenchmark.intStream   avgt   28  9094.313 ± 83.861  us/op
IteratorBenchmark.iterator    avgt   28  1714.772 ± 15.060  us/op
IteratorBenchmark.stream      avgt   28  1696.980 ± 14.129  us/op
     */

    private static final int ARRAY_SIZE = 1_000_000;

    private List<Integer> array;

    @Setup
    public void setUp() {
        array = new ArrayList<>(ARRAY_SIZE);
        IntStream.range(0, ARRAY_SIZE).forEach(i -> {
            array.add(ThreadLocalRandom.current().nextInt());
        });
    }

    @Benchmark
    public void classicFor(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            count += array.get(i);
        }
        bh.consume(count);
    }

    @Benchmark
    public void forLoop(Blackhole bh) {
        int count = 0;
        for (int i : array) {
            count += i;
        }
        bh.consume(count);
    }

    @Benchmark
    public void iterator(Blackhole bh) {
        int count = 0;
        Iterator<Integer> iter = array.iterator();
        while (iter.hasNext()) {
            count += iter.next();
        }
        bh.consume(count);
    }

    @Benchmark
    public void intStream(Blackhole bh) {
        AtomicInteger count = new AtomicInteger();
        IntStream.range(0, ARRAY_SIZE).forEach(i -> {
            count.addAndGet(array.get(i));
        });
        bh.consume(count);
    }

    @Benchmark
    public void stream(Blackhole bh) {
        int count = array.stream().mapToInt(Integer::intValue).sum();
        bh.consume(count);
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(IteratorBenchmark.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }
}
