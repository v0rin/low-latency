package com.vorin.concurrency.producer;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Adam
 */
public class Producer implements IProducer {

    private String name;
    private int maxMsgCount;
    private Queue<String> queue;
    private long produceIntervalMs;

    private int producedMsgCount;

    Producer(String name, int maxMsgCount, Queue<String> queue) {
        this(name, maxMsgCount, queue, 0);
    }

    Producer(String name, int maxMsgCount, Queue<String> queue, long produceIntervalMs) {
        this.name = name;
        this.maxMsgCount = maxMsgCount;
        this.queue = queue;
        this.produceIntervalMs = produceIntervalMs;
    }


    @Override
    public void startProducing() {
        while (producedMsgCount < maxMsgCount) {
            producedMsgCount++;
            queue.offer(name + ": msg no " + producedMsgCount);
            System.out.println(name + " produced msg no " + producedMsgCount);

            if (produceIntervalMs <= 0) continue;
            try {
                TimeUnit.MILLISECONDS.sleep(produceIntervalMs);
            }
            catch (InterruptedException e) {
                System.out.println(name + " interrupted e=" + e);
                Thread.currentThread().interrupt();
            }
        }
    }
}