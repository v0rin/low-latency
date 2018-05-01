package com.vorin.lowlatency;

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
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class EnumSwitchBenchmark {
    /**
     * I couldn't see any difference really between enum, byte, short, int, long
     * So the only difference really would be memory, which is important of course
     *
     * Switch for 3 items was almost 10% slower than if
     */

    private static final int ARRAY_SIZE = 1_000_000;

    private static final short VAL1 = 0x0;
    private static final short VAL2 = 0x1;
    private static final short VAL3 = 0x2;

    private static final long VAL11 = 0x0;
    private static final long VAL22 = 0x1;
    private static final long VAL33 = 0x2;

    enum TestEnum {
        VAL1, VAL2, VAL3;
    }

    private TestEnum[] enums;
    private short[] constants;
    private long[] longs;

    @Setup
    public void setUp() {
        enums = new TestEnum[ARRAY_SIZE];
        constants = new short[ARRAY_SIZE];
        longs = new long[ARRAY_SIZE];
        IntStream.range(0, ARRAY_SIZE).forEach(i -> {
            enums[i] = getRandomTestEnum();
            constants[i] = getRandomConstant();
            longs[i] = getRandomLong();
        });
    }

    @Benchmark
    public void constantsIf(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            if (constants[i] == VAL1) count += 1;
            else if (constants[i] == VAL2) count += 2;
            else if (constants[i] == VAL3) count += 3;
        }
        bh.consume(count);
    }

    @Benchmark
    public void constantsSwitch(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            switch (constants[i]) {
                case VAL1:
                    count += 1;
                    break;
                case VAL2:
                    count += 2;
                    break;
                default:
                    count += 3;
            }
        }
        bh.consume(count);
    }

//    @Benchmark
    public void longs(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            if (longs[i] == VAL1) count += 1;
            else if (longs[i] == VAL2) count += 2;
            else if (longs[i] == VAL3) count += 3;
        }
        bh.consume(count);
    }

//    @Benchmark
    public void enums(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            if (enums[i] == TestEnum.VAL1) count += 1;
            else if (enums[i] == TestEnum.VAL2) count += 2;
            else if (enums[i] == TestEnum.VAL3) count += 3;
        }
        bh.consume(count);
    }

    private TestEnum getRandomTestEnum() {
        double random = ThreadLocalRandom.current().nextDouble();
        if (random < 0.33) return TestEnum.VAL1;
        if (random >= 0.33 && random < 0.66) return TestEnum.VAL2;
        else return TestEnum.VAL3;
    }

    private short getRandomConstant() {
        double random = ThreadLocalRandom.current().nextDouble();
        if (random < 0.33) return VAL1;
        if (random >= 0.33 && random < 0.66) return VAL2;
        else return VAL3;
    }

    private long getRandomLong() {
        double random = ThreadLocalRandom.current().nextDouble();
        if (random < 0.33) return VAL11;
        if (random >= 0.33 && random < 0.66) return VAL22;
        else return VAL33;
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(EnumSwitchBenchmark.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }
}
