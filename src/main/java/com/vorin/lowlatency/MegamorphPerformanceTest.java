package com.vorin.lowlatency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@SuppressWarnings("checkstyle:magicnumber")
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MegamorphPerformanceTest {
    @Param({ "1000000" })
    private int count;

    @Param({"false", "true"})
    private boolean pollute;

    private Integer[] input;

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(MegamorphPerformanceTest.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }


    @Setup
    public void setUp() {
        Random r = new Random(1);
        input = r.ints(count).boxed().toArray(Integer[]::new);
        if(pollute) {
            // pollute "filter" with different predicates
            for(int i=0; i<500; i++) Stream.of(1,2,3).filter(x -> x > 1).collect(Collectors.toList());
            for(int i=0; i<500; i++) Stream.of(1,2,3).filter(x -> x > 2).collect(Collectors.toList());
            for(int i=0; i<500; i++) Stream.of(1,2,3).filter(x -> x > 3).collect(Collectors.toList());
        }
    }

    @Benchmark
    public void baseline(Blackhole bh) {
        List<Integer> result = new ArrayList<>();
        for(int val : input) {
            if(val > 0) result.add(val);
        }
        bh.consume(result);
    }

    @Benchmark
    public void stream(Blackhole bh) {
        bh.consume(Stream.of(input).filter(x -> x > 0).collect(Collectors.toList()));
    }
}
