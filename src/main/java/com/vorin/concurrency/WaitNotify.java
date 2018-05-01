package com.vorin.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;

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
public class WaitNotify {

    private static final int MSG_COUNT = 1000;

    private static class Baton {
        boolean isConsumed;
    }

    private static class Producer implements Callable<Integer> {
        private String name;
        private Baton baton;
        int counter;

        Producer(String name, Baton baton) {
            this.name = name;
            this.baton = baton;
        }

        @Override
        public Integer call() {
            while (counter < MSG_COUNT) {
                synchronized (baton) {
                    while (!baton.isConsumed) {
                        try {
//                            System.out.println(name + " waiting... " + counter);
                            baton.wait();
                        }
                        catch (InterruptedException e) {
                            System.out.println(name + " awoken " + counter);
                        }
                    }
                    produce();
                }
            }
            return counter;
        }

        private void produce() {
            WaitNotify.sleep(900);
            counter++;
            System.out.println(name + " produces " + counter);
            baton.isConsumed = false;
            baton.notify();
        }
    }

    private static class Consumer implements Callable<Integer> {
        private String name;
        private Baton baton;
        int counter;

        Consumer(String name, Baton baton) {
            this.name = name;
            this.baton = baton;
        }

        @Override
        public Integer call() {
            while (counter < MSG_COUNT) {
                synchronized (baton) {
                    while (baton.isConsumed) {
                        try {
//                            System.out.println(name + " waiting... " + counter);
                            baton.wait();
                        }
                        catch (InterruptedException e) {
                            System.out.println(name + " awoken " + counter);
                        }
                    }
                    consume();
                }
            }

            return counter;
        }

        private void consume() {
            WaitNotify.sleep(1000);
            counter++;
            System.out.println(name + " consumed " + counter);
            baton.isConsumed = true;
            baton.notify();
        }
    }

    private static void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Benchmark
    public void pingpong() throws InterruptedException, ExecutionException {
        Baton baton = new Baton();
        Producer producer1 = new Producer("Producer1", baton);
        Consumer consumer1 = new Consumer("Consumer1", baton);
        baton.isConsumed = true;

        ExecutorService es = Executors.newCachedThreadPool();

        Future<Integer> producer1MsgCountFuture = es.submit(producer1);
        Future<Integer> consumer1MsgCountFuture = es.submit(consumer1);

        System.out.println("Producer1.counter=" + producer1MsgCountFuture.get());
        System.out.println("Consumer1.counter=" + consumer1MsgCountFuture.get());

        es.shutdown();
    }

    public void stampedLock() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        StampedLock lock = new StampedLock();

        executor.submit(() -> {
            long stamp = lock.tryOptimisticRead();
            try {
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                sleep(1);
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                sleep(2);
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
            } finally {
                lock.unlock(stamp);
            }
        });

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                System.out.println("Write Lock acquired");
                sleep(2);
            } finally {
                lock.unlock(stamp);
                System.out.println("Write done");
            }
        });

        executor.shutdown();
    }

    private void semaphore() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        Semaphore semaphore = new Semaphore(5);

        Runnable longRunningTask = () -> {
            boolean permit = false;
            try {
                permit = semaphore.tryAcquire(1, TimeUnit.SECONDS);
                if (permit) {
                    System.out.println("Semaphore acquired");
                    sleep(5);
                } else {
                    System.out.println("Could not acquire semaphore");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                if (permit) {
                    semaphore.release();
                }
            }
        };

        IntStream.range(0, 10).forEach(i -> executor.submit(longRunningTask));

        executor.shutdown();
    }


    public static void main(String[] args) throws InterruptedException, RunnerException, ExecutionException {
        new WaitNotify().pingpong();
//        new WaitNotify().stampedLock();
//        new WaitNotify().semaphore();

//        Options opts = new OptionsBuilder()
//                .include(WaitNotify.class.getName() + ".*")
//                .shouldDoGC(true)
//                .build();
//
//        new Runner(opts).run();
    }
}
