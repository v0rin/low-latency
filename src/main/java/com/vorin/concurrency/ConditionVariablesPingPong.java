package com.vorin.concurrency;

import static java.lang.System.out;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.HdrHistogram.Histogram;


public class ConditionVariablesPingPong
{
    private static final int REPETITIONS = 2_000_000;

    private static final Lock pingLock = new ReentrantLock();
    private static final Condition pingCondition = pingLock.newCondition();

    private static final Lock pongLock = new ReentrantLock();
    private static final Condition pongCondition = pongLock.newCondition();

    private static final Histogram histogram = new Histogram(3);

    private static long pingValue = -1;
    private static long pongValue = -1;

    public static void main(final String[] args) throws Exception
    {
        for (int i = 0; i < 5; i++)
        {
            testRun();
        }
    }

    private static void testRun() throws InterruptedException
    {
        pingValue = -1;
        pongValue = -1;
        histogram.reset();

        final Thread sendThread = new Thread(new PingRunner());
        final Thread echoThread = new Thread(new PongRunner());
        echoThread.start();
        sendThread.start();

        echoThread.join();

        out.println("pingValue = " + pingValue + ", pongValue = " + pongValue);
        System.out.println("Histogram of RTT latencies in microseconds.");

        histogram.outputPercentileDistribution(System.out, 1000.0);
    }

    public static class PingRunner implements Runnable
    {
        @Override
        public void run()
        {
            for (long i = 0; i < REPETITIONS; i++)
            {
                final long start = System.nanoTime();

                pingLock.lock();
                try
                {
                    pingValue = i;
                    pingCondition.signal();
                }
                finally
                {
                    pingLock.unlock();
                }

                pongLock.lock();
                try
                {
                    while (pongValue != i)
                    {
                        pongCondition.await();
                    }
                }
                catch (final InterruptedException ex)
                {
                    break;
                }
                finally
                {
                    pongLock.unlock();
                }

                final long duration = System.nanoTime() - start;
                histogram.recordValue(duration);
            }
        }
    }

    public static class PongRunner implements Runnable
    {
        @Override
        public void run()
        {
            for (long i = 0; i < REPETITIONS; i++)
            {
                pingLock.lock();
                try
                {
                    while (pingValue != i)
                    {
                        pingCondition.await();
                    }
                }
                catch (final InterruptedException ex)
                {
                    break;
                }
                finally
                {
                    pingLock.unlock();
                }

                pongLock.lock();
                try
                {
                    pongValue = i;
                    pongCondition.signal();
                }
                finally
                {
                    pongLock.unlock();
                }
            }
        }
    }
}