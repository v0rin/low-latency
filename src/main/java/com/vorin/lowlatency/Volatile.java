package com.vorin.lowlatency;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
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
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class Volatile {

    private int nonVolatileInt;

    private volatile int volatileInt;

    @Benchmark
    public void nonVolatileInt(Blackhole bh) {
        bh.consume(++nonVolatileInt);
    }

    @Benchmark
    public void volatileInt(Blackhole bh) {
        bh.consume(++volatileInt);
    }


    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(Volatile.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }

}
