package com.vorin.concurrency;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Adam
 */
public class FalseSharingTest extends Thread {

    private static final int CACHE_LINE_LENGTH = 64;
    private static final int LONG_SIZE = 8;
    private static final int SINGLE_LONG_PADDING = (CACHE_LINE_LENGTH - LONG_SIZE) / LONG_SIZE;

    private static class DataHolder {
        @sun.misc.Contended
        public volatile long l1 = 0;
        @sun.misc.Contended
        public volatile long l2 = 0;
        @sun.misc.Contended
        public volatile long l3 = 0;
        @sun.misc.Contended
        public volatile long l4 = 0;
    }
    private static final long nLoops = 1_00_000_000;

    private static DataHolder dh = new DataHolder();

    public FalseSharingTest(Runnable r) {
        super(r);
    }

    public static void main(String[] args) throws Exception {
        List<FalseSharingTest> tests = new ArrayList<>();
        tests.add(new FalseSharingTest(() -> {
            for (long i = 0; i < nLoops; i++) {
                dh.l1 += i;
            }
        }));
        tests.add(new FalseSharingTest(() -> {
            for (long i = 0; i < nLoops; i++) {
                dh.l2 += i;
            }
        }));
        tests.add(new FalseSharingTest(() -> {
            for (long i = 0; i < nLoops; i++) {
                dh.l3 += i;
            }
        }));
        tests.add(new FalseSharingTest(() -> {
            for (long i = 0; i < nLoops; i++) {
                dh.l4 += i;
            }
        }));

        long then = System.currentTimeMillis();
        for (FalseSharingTest ct : tests) {
            ct.start();
        }
        for (FalseSharingTest ct : tests) {
            ct.join();
        }

        long now = System.currentTimeMillis();
        System.out.println("Duration: " + (now - then) + " ms");
    }
}