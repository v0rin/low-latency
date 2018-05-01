package com.vorin.concurrency.producer;

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
import org.openjdk.jmh.profile.StackProfiler;
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
public class ProducerBenchmark {

    private static final int MAX_MSG_COUNT = 10000;

    @Benchmark
    public void runTwoSynchronizedProducersTwoConsumers() throws InterruptedException {
        int finalQueueSize = ProducerConsumer.runTwoSynchronizedProducersTwoConsumers(MAX_MSG_COUNT);
        if (finalQueueSize != 0) {
            System.out.println("Queue size incorrect! Should be 0 but is " + finalQueueSize);
        }
    }

//    @Benchmark
//    public void runTwoSynchronizedProducers() throws InterruptedException {
//        int finalQueueSize = ProducerConsumer.runTwoSynchronizedProducers(MAX_MSG_COUNT);
//        if (finalQueueSize != 2*MAX_MSG_COUNT) {
//            System.out.println("Queue size incorrect! Should be 0 but is " + finalQueueSize);
//        }
//    }

    @Benchmark
    public void runTwoProducersTwoConsumers() throws InterruptedException {
        int finalQueueSize = ProducerConsumer.runTwoProducersTwoConsumers(MAX_MSG_COUNT);
        if (finalQueueSize != 0) {
            System.out.println("Queue size incorrect! Should be 0 but is " + finalQueueSize);
        }
    }

//    @Benchmark
//    public void runTwoProducers() throws InterruptedException {
//        int finalQueueSize = ProducerConsumer.runTwoProducers(MAX_MSG_COUNT);
//        if (finalQueueSize != 2*MAX_MSG_COUNT) {
//            System.out.println("Queue size incorrect! Should be 0 but is " + finalQueueSize);
//        }
//    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(ProducerBenchmark.class.getName() + ".*")
                .shouldDoGC(true)
                .addProfiler(StackProfiler.class)
                .build();

        new Runner(opts).run();
    }
}
