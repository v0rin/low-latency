package com.vorin.lowlatency;

import java.util.ArrayList;
import java.util.List;
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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 *
 * @author Adam
 */

@SuppressWarnings("checkstyle:all")
@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class Arrays {

    private static final int MASK = 1 << 12 - 1;

    @Param({"1000", "100000"})
    private int size;

    List<Integer> arrayList;
    IntList intList;
    Integer[] arrayInteger;
    int[] arrayInt;

    @Setup
    public void setUp() {
        arrayList = new ArrayList<>();
        for (int i = 0; i < size; i++) arrayList.add(0);

        intList = new IntArrayList();
        for (int i = 0; i < size; i++) intList.add(0);

        arrayInteger = new Integer[size];

        arrayInt = new int[size];
    }

    @Benchmark
    public void arrayList() {
        for (int i = 0; i < size; i++) {
            arrayList.set(i, i & MASK);
        }
    }

    @Benchmark
    public void arrayInt() {
        for (int i = 0; i < size; i++) {
            arrayInt[i] = i & MASK;
        }
    }

    @Benchmark
    public void arrayInteger() {
        for (int i = 0; i < size; i++) {
            arrayInteger[i] = i & MASK;
        }
    }

    @Benchmark
    public void intList() {
        for (int i = 0; i < size; i++) {
            intList.set(i, i & MASK);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(Arrays.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }

}
