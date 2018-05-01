package com.vorin.concurrency.producer;

import java.util.Queue;

/**
 *
 * @author Adam
 */
public class Consumer implements IConsumer {

    private String name;
    private Queue<String> queue;

    private int consumedMsgCount;
    private int emptyPollCount;
    private Flag isProducingInProgress;

    Consumer(String name, Queue<String> queue, Flag isProducingInProgress) {
        this.name = name;
        this.queue = queue;
        this.isProducingInProgress = isProducingInProgress;
    }

    @Override
    public void startConsuming() {
        while (isProducingInProgress.isSet() || !queue.isEmpty()) {
            String msg = queue.poll();
            if (msg == null) {
                emptyPollCount++;
                continue;
            }
            consumedMsgCount++;
            System.out.println(name + " consumed msg=" + msg + " / " + consumedMsgCount);
        }
//        System.out.println(name + " consumedMsgCount=" + consumedMsgCount + "; emptyPollCount=" + emptyPollCount);
    }

}
