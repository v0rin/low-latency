package com.vorin.lowlatency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
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
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class AtomicIntegerBenchmark {
/*
# Run complete. Total time: 00:00:58
Benchmark                                                Mode  Cnt   Score   Error  Units
AtomicIntegerBenchmark.atomicIntegerCounter              avgt    5  22.111 ± 1.734  ms/op
AtomicIntegerBenchmark.baselineNotSynchronizedCounter    avgt    5   1.040 ± 0.223  ms/op
AtomicIntegerBenchmark.notContendedAtomicIntegerCounter  avgt    5   9.010 ± 0.557  ms/op
AtomicIntegerBenchmark.synchronizedIntegerCounter        avgt    5  44.444 ± 5.661  ms/op
AtomicIntegerBenchmark.synchronizedNoContenion           avgt    5   2.413 ± 0.198  ms/op
 */

    private static final int MAX_COUNT = 1_000_000;

    @Benchmark
    public void atomicIntegerCounter() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        AtomicInteger counter = new AtomicInteger();
        Callable<Integer> incrementor = () -> {
            int incrementCount = 0;
            while (counter.getAndIncrement() < MAX_COUNT) incrementCount++;
            return incrementCount;
        };
        Future<Integer> incrementCount1Future = executorService.submit(incrementor);
        Future<Integer> incrementCount2Future = executorService.submit(incrementor);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        if (counter.get() > MAX_COUNT + 2) {
            throw new RuntimeException(String.format("counter=%s, thread1=%s, thread2=%s",
                                       counter, incrementCount1Future.get(), incrementCount2Future.get()));
        }
    }

    @Benchmark
    public void notContendedAtomicIntegerCounter() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        AtomicInteger counter = new AtomicInteger();
        Callable<Integer> incrementor = () -> {
            int incrementCount = 0;
            while (counter.getAndIncrement() < MAX_COUNT) incrementCount++;
            return incrementCount;
        };
        Future<Integer> incrementCount1Future = executorService.submit(incrementor);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        if (counter.get() > MAX_COUNT + 2) {
            throw new RuntimeException(String.format("counter=%s, thread1=%s",
                                       counter, incrementCount1Future.get()));
        }
    }

    private static class NotSynchronizedCounter {
        private int count;

        public int getAndIncrement() {
            return count++;
        }

        public int get() {
            return count;
        }
    }

    private static class Counter {
        private int count;

        public synchronized int getAndIncrement() {
            return count++;
        }

        public int get() {
            return count;
        }
    }

    @Benchmark
    public void synchronizedIntegerCounter() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Counter counter = new Counter();
        Callable<Integer> incrementor = () -> {
            int incrementCount = 0;
            while (counter.getAndIncrement() < MAX_COUNT) incrementCount++;
            return incrementCount;
        };
        Future<Integer> incrementCount1Future = executorService.submit(incrementor);
        Future<Integer> incrementCount2Future = executorService.submit(incrementor);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        if (counter.get() > MAX_COUNT + 2) {
            throw new RuntimeException(String.format("counter=%s, thread1=%s, thread2=%s",
                                       counter, incrementCount1Future.get(), incrementCount2Future.get()));
        }
    }

    @Benchmark
    public void synchronizedNoContenion() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Counter counter = new Counter();
        Callable<Integer> incrementor = () -> {
            int incrementCount = 0;
            while (counter.getAndIncrement() < MAX_COUNT) incrementCount++;
            return incrementCount;
        };
        Future<Integer> incrementCount1Future = executorService.submit(incrementor);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        if (counter.get() > MAX_COUNT + 2) {
            throw new RuntimeException(String.format("counter=%s, thread1=%s",
                                       counter, incrementCount1Future.get()));
        }
    }

    @Benchmark
    public void baselineNotSynchronizedCounter() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        NotSynchronizedCounter counter = new NotSynchronizedCounter();
        Callable<Integer> incrementor = () -> {
            int incrementCount = 0;
            while (counter.getAndIncrement() < MAX_COUNT) incrementCount++;
            return incrementCount;
        };
        Future<Integer> incrementCount1Future = executorService.submit(incrementor);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        if (counter.get() > MAX_COUNT + 2) {
            throw new RuntimeException(String.format("counter=%s, thread1=%s",
                                       counter, incrementCount1Future.get()));
        }
    }


    public static void main(String[] args) throws RunnerException, InterruptedException, ExecutionException {

        Options opts = new OptionsBuilder()
                .include(AtomicIntegerBenchmark.class.getName() + ".*")
                .shouldDoGC(true)
                .build();

        new Runner(opts).run();
    }

}
