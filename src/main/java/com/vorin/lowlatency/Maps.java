package com.vorin.lowlatency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.carrotsearch.hppc.IntIntHashMap;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 *
 * @author Adam
 */

@SuppressWarnings("checkstyle:all")
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class Maps {

    private static final int MASK = 1 << 12 - 1;

    @Param({"500000"})
    private int size;

    Map<Integer, Integer> hashMap;
    Map<Integer, Integer> concurrentHashMap;
    Int2IntFunction int2IntLinkedOpenHashMap;
    Int2IntOpenHashMap int2IntOpenHashMap;
    IntIntHashMap intIntHashMap;

    @Setup
    public void setUp() {
        hashMap = new HashMap<>();
        for (int i = 0; i < size; i++) hashMap.put(i, i & MASK);

        concurrentHashMap = new ConcurrentHashMap<>();
        for (int i = 0; i < size; i++) concurrentHashMap.put(i, i & MASK);

//        int2intMap = new Int2IntAVLTreeMap(); - slow get for 10_000
//        int2intMap = new Int2IntArrayMap(); - slow get
//        int2intMap = new Int2IntRBTreeMap(); - slow get
        // the below maps also get slower as the size grows, the breakeven point with java.util.Maps seems to be about 100k
        int2IntLinkedOpenHashMap = new Int2IntLinkedOpenHashMap(size, Hash.VERY_FAST_LOAD_FACTOR);
        for (int i = 0; i < size; i++) int2IntLinkedOpenHashMap.put(i, i & MASK);

        int2IntOpenHashMap = new Int2IntOpenHashMap(size, Hash.VERY_FAST_LOAD_FACTOR);
        for (int i = 0; i < size; i++) int2IntOpenHashMap.put(i, i & MASK);

        intIntHashMap = new IntIntHashMap();
        for (int i = 0; i < size; i++) intIntHashMap.put(i, i & MASK);

    }

//    @Benchmark
//    public void hashMap(Blackhole bh) {
//        for (int i = 0; i < size; i++) {
//            bh.consume(hashMap.get(i));
//        }
//    }
//
    @Benchmark
    public void intIntHashMap(Blackhole bh) {
        for (int i = 0; i < size; i++) {
            bh.consume(intIntHashMap.get(i));
        }
    }


//    @Benchmark
//    public void concurrentHashMap(Blackhole bh) {
//        for (int i = 0; i < size; i++) {
//            bh.consume(concurrentHashMap.get(i));
//        }
//    }
//
//    @Benchmark
//    public void int2IntLinkedOpenHashMap(Blackhole bh) {
//        for (int i = 0; i < size; i++) {
//            bh.consume(int2IntLinkedOpenHashMap.get(i));
//        }
//    }
//
//    @Benchmark
//    public void int2IntOpenHashMap(Blackhole bh) {
//        for (int i = 0; i < size; i++) {
//            bh.consume(int2IntOpenHashMap.get(i));
//        }
//    }



    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(Maps.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }

}
