package com.vorin.lowlatency;

import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.annotations.TearDown;
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
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Thread)
public class WriteCombining {
    private static final int ITEMS = 1 << 12;
    private static final int MASK = ITEMS - 1;

    private static final byte[] arrayA = new byte[ITEMS];
    private static final byte[] arrayB = new byte[ITEMS];
    private static final byte[] arrayC = new byte[ITEMS];
    private static final byte[] arrayD = new byte[ITEMS];
    private static final byte[] arrayE = new byte[ITEMS];
    private static final byte[] arrayF = new byte[ITEMS];

    @Param({ "100000" })
    private int loopCount;


    @Setup
    public void setUp() {
    }


    @TearDown
    public void tearDown() {
    }


    @Benchmark
    public void baseline(Blackhole bh) {
        int i = loopCount;
        while (--i != 0)
        {
            int slot = i & MASK;
            byte b = (byte)i;
            arrayA[slot] = b;
            arrayB[slot] = b;
            arrayC[slot] = b;
            arrayD[slot] = b;
            arrayE[slot] = b;
            arrayF[slot] = b;
            bh.consume(arrayA);
            bh.consume(arrayB);
            bh.consume(arrayC);
            bh.consume(arrayD);
            bh.consume(arrayE);
            bh.consume(arrayF);
        }
    }


    @Benchmark
    public void compare(Blackhole bh) {
        int i = loopCount;
        while (--i != 0)
        {
            int slot = i & MASK;
            byte b = (byte)i;
            arrayA[slot] = b;
            arrayB[slot] = b;
            arrayC[slot] = b;
            bh.consume(arrayA);
            bh.consume(arrayB);
            bh.consume(arrayC);
        }

        i = loopCount;
        while (--i != 0)
        {
            int slot = i & MASK;
            byte b = (byte)i;
            arrayD[slot] = b;
            arrayE[slot] = b;
            arrayF[slot] = b;
            bh.consume(arrayD);
            bh.consume(arrayE);
            bh.consume(arrayF);
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(WriteCombining.class.getName() + ".*")
//                .addProfiler(StackProfiler.class)
//                .addProfiler(GCProfiler.class)
//                .addProfiler(ClassloaderProfiler.class)
//                .addProfiler(CompilerProfiler.class)
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }

}
