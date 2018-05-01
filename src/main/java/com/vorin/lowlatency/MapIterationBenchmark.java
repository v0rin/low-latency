package com.vorin.lowlatency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(3)
@State(Scope.Benchmark)
public class MapIterationBenchmark {
    /**
# Run complete. Total time: 00:01:27
Benchmark                       Mode  Cnt     Score    Error  Units
MapIterationBenchmark.entrySet  avgt   15   767.465 ±  8.268  us/op
MapIterationBenchmark.keySet    avgt   15  1102.754 ± 38.559  us/op
MapIterationBenchmark.values    avgt   15   751.215 ± 13.343  us/op
     */

    private static final int MAP_SIZE = 1_00_000;

    private Map<Integer, Integer> map;

    @Setup
    public void setUp() {
        map = new HashMap<>();
        IntStream.range(0, MAP_SIZE).forEach(i -> {
            map.put(i, ThreadLocalRandom.current().nextInt());
        });
    }

    @Benchmark
    public void entrySet(Blackhole bh) {
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            count += entry.getValue();
        }
        bh.consume(count);
    }

    @Benchmark
    public void values(Blackhole bh) {
        int count = 0;
        for (Integer value : map.values()) {
            count += value;
        }
        bh.consume(count);
    }

    @Benchmark
    public void keySet(Blackhole bh) {
        int count = 0;
        for (Integer key : map.keySet()) {
            count += map.get(key);
        }
        bh.consume(count);
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(MapIterationBenchmark.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }
}
