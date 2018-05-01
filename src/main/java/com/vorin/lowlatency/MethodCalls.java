package com.vorin.lowlatency;

import java.util.concurrent.ThreadLocalRandom;
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
@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MethodCalls {

    private OneLongMethod oneLongMethod = new OneLongMethod();
    private MultipleShortMethods multipleShortMethods = new MultipleShortMethods();

    @Benchmark
    public void baseline(Blackhole bh) {
        bh.consume(ThreadLocalRandom.current().nextInt(0, 50));
    }

//    @Benchmark
//    public void oneLongMethod(Blackhole bh) {
//        bh.consume(oneLongMethod.run(ThreadLocalRandom.current().nextInt(0, 50)));
//    }
//
    @Benchmark
    public void multipleShortMethods(Blackhole bh) {
        bh.consume(multipleShortMethods.run(ThreadLocalRandom.current().nextInt(0, 50)));
    }


    private static class OneLongMethod {
        int run(int i) {
            if (i >= 0 && i < 10) return 100;
            else if (i >=10 && i < 20) return 200;
            else return 300;
        }
    }


    private static class MultipleShortMethods {
        int run(int i) {
            if (betweenZeroAndTen(i)) return 100;
            else if (betweenTenAndTwenty(i)) return 200;
            else return 300;
        }

        private boolean betweenZeroAndTen(int i) {
            return i >= 0 && i < 10;
        }

        private boolean betweenTenAndTwenty(int i) {
            return i >=10 && i < 20;
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(MethodCalls.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }

}
