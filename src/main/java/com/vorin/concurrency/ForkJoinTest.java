package com.vorin.concurrency;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

/**
 *
 * @author Adam
 */
public class ForkJoinTest {

    private double[] d;

    ForkJoinTest(double[] d) {
        this.d = d;
    }

    public static void main(String[] args) {
        final int size = 300_000_000;
        double[] d = createArrayOfRandomDoubles(size);

//        ForkJoinTest test = new ForkJoinTest(d);
//        Stopwatch sw = Stopwatch.createStarted();
//        int n = new ForkJoinPool().invoke(test.new ForkJoinTask(0, size));
//        long elapsed = sw.elapsed(TimeUnit.MILLISECONDS);
//        System.out.println(String.format("Found %s values in %s ms", n, elapsed));

        Stopwatch sw = Stopwatch.createStarted();
        int n = count(d);
        long elapsed = sw.elapsed(TimeUnit.MILLISECONDS);
        System.out.println(String.format("Found %s values in %s ms", n, elapsed));
    }

    private static int count(double[] d) {
        int count = 0;
        for (int i = 0; i < d.length; i++) {
            if (d[i] < 0.5) count++;
        }
        return count;
    }

    private static double[] createArrayOfRandomDoubles(int size) {
        double[] d = new double[size];
        for (int i = 0; i < size; i++) {
            d[i] = ThreadLocalRandom.current().nextDouble();
        }
        return d;
    }

    private class ForkJoinTask extends RecursiveTask<Integer> {

        private static final int DELEGATE_THRESHOLD = 10;

        private int first;
        private int last;

        ForkJoinTask(int first, int last) {
            this.first = first;
            this.last = last;
        }

        @Override
        protected Integer compute() {
            int subCount = 0;
            if (last - first < DELEGATE_THRESHOLD) {
                for (int i = first; i < last; i++) {
                    if (d[i] < 0.5) subCount++;
                }
            }
            else {
                int mid = (first + last) >>> 1;
                ForkJoinTask left = new ForkJoinTask(first, mid);
                left.fork();
                ForkJoinTask right = new ForkJoinTask(mid, last);
                right.fork();
                subCount = left.join();
                subCount += right.join();
            }
            return subCount;
        }

    }

}
