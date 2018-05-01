package com.vorin.concurrency.producer;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 * @author Adam
 */
public class ProducerConsumer {

    private static final int MAX_MSG_COUNT = 10;

    public static int runTwoSynchronizedProducers(int msgCount) throws InterruptedException {
        Queue<String> queue = new ArrayDeque<>();

        IProducer producer1 = new SynchronizedProducer("Producer1", msgCount, queue);
        Thread producerThread1 = new Thread(producer1::startProducing);

        IProducer producer2 = new SynchronizedProducer("Producer2", msgCount, queue);
        Thread producerThread2 = new Thread(producer2::startProducing);

        producerThread1.start();
        producerThread2.start();

        producerThread1.join();
        producerThread2.join();

        return queue.size();
    }

    public static int runTwoSynchronizedProducersTwoConsumers(int msgCount) throws InterruptedException {
        Queue<String> queue = new ArrayDeque<>();
        Flag isProducingInProgress = new Flag(true);

        IProducer producer1 = new SynchronizedProducer("Producer1", msgCount, queue);
        Thread producerThread1 = new Thread(producer1::startProducing);

        IProducer producer2 = new SynchronizedProducer("Producer2", msgCount, queue);
        Thread producerThread2 = new Thread(producer2::startProducing);


        IConsumer consumer1 = new SynchronizedConsumer("Consumer1", queue, isProducingInProgress);
        Thread consumerThread1 = new Thread(consumer1::startConsuming);

        IConsumer consumer2 = new SynchronizedConsumer("Consumer2", queue, isProducingInProgress);
        Thread consumerThread2 = new Thread(consumer2::startConsuming);


        producerThread1.start();
        producerThread2.start();
        consumerThread1.start();
        consumerThread2.start();

        producerThread1.join();
        producerThread2.join();

        isProducingInProgress.unset();

        consumerThread1.join();
        consumerThread2.join();

        return queue.size();
    }

    public static int runTwoProducers(int msgCount) throws InterruptedException {
        Queue<String> queue = new ConcurrentLinkedDeque<>();

        IProducer producer1 = new Producer("Producer1", msgCount, queue);
        Thread producerThread1 = new Thread(producer1::startProducing);

        IProducer producer2 = new Producer("Producer2", msgCount, queue);
        Thread producerThread2 = new Thread(producer2::startProducing);

        producerThread1.start();
        producerThread2.start();

        producerThread1.join();
        producerThread2.join();

        return queue.size();
    }

    public static int runTwoProducersTwoConsumers(int msgCount) throws InterruptedException {
        Queue<String> queue = new ConcurrentLinkedDeque<>();
        Flag isProducingInProgress = new Flag(true);

        IProducer producer1 = new Producer("Producer1", msgCount, queue);
        Thread producerThread1 = new Thread(producer1::startProducing);

        IProducer producer2 = new Producer("Producer2", msgCount, queue);
        Thread producerThread2 = new Thread(producer2::startProducing);


        IConsumer consumer1 = new Consumer("Consumer1", queue, isProducingInProgress);
        Thread consumerThread1 = new Thread(consumer1::startConsuming);

        IConsumer consumer2 = new Consumer("Consumer2", queue, isProducingInProgress);
        Thread consumerThread2 = new Thread(consumer2::startConsuming);


        producerThread1.start();
        producerThread2.start();
        consumerThread1.start();
        consumerThread2.start();

        producerThread1.join();
        producerThread2.join();

        isProducingInProgress.unset();

        consumerThread1.join();
        consumerThread2.join();

        return queue.size();
    }

    public static void main(String[] args) throws InterruptedException {
//        runTwoSynchronizedProducersTwoConsumers(MAX_MSG_COUNT);
        runTwoProducersTwoConsumers(MAX_MSG_COUNT);
    }
}
