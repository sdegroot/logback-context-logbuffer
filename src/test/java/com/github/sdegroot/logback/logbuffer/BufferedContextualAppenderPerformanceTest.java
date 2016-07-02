package com.github.sdegroot.logback.logbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.Mockito.mock;

/**
 * This is a "performance test" that doesn't assert anything. It just shows the differences a bit.
 */
public class BufferedContextualAppenderPerformanceTest {

    private BufferedContextualAppender bufferedContextualAppender;
    private BufferedAppenderWrapper stubAppender;
    private StopWatch stopWatch;

    @Before
    public void setUp() {
        stubAppender = mock(BufferedAppenderWrapper.class);
        bufferedContextualAppender = new BufferedContextualAppender(stubAppender, 50);
        bufferedContextualAppender.setBufferFrom(Level.DEBUG);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.WARN);
        bufferedContextualAppender.setLogMessagesAfterFlush(true);
        bufferedContextualAppender.setDropBelowBufferFrom(true);

        stopWatch = new StopWatch();
    }

    @Test
    public void Calls1000WithoutBufferFlush() {
        stopWatch.start();

        for (int i = 0; i < 1000; i++) {
            bufferedContextualAppender.doAppend(createLoggingEvent(Level.DEBUG));
        }

        stopWatch.stop();
        ;
        printStats(stopWatch, "1000 without buffer flush");
    }

    @Test
    public void DirectCalls1000WithoutBuffer() {
        stopWatch.start();

        for (int i = 0; i < 1000; i++) {
            stubAppender.appendDirectly(createLoggingEvent(Level.DEBUG));
        }

        stopWatch.stop();
        ;
        printStats(stopWatch, "1000 without buffer");
    }

    @Test
    public void Calls1000WithBufferFlushEvery50() {
        stopWatch.start();

        for (int i = 0; i < 1000; i++) {
            Level level = Level.DEBUG;
            if (i % 50 == 0) {
                level = Level.WARN; // trigger buffer flush
            }
            bufferedContextualAppender.doAppend(createLoggingEvent(level));

        }

        stopWatch.stop();
        printStats(stopWatch, "1000 with buffer flush");
    }

    @Test
    public void MultithreadedCalls100000WithBufferFlushEvery50() throws InterruptedException {
        stopWatch.start();

        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final AtomicLong totalCount = new AtomicLong(0);

        final Runnable threadCode = new Runnable() {
            @Override
            public void run() {
                final long frequency = Math.round(Math.random() * 10) + 1;
                final int amount = 1000;
                System.out.println("Using " + frequency + " as flush frequency");
                for (int i = 0; i < amount; i++) {
                    Level level = Level.DEBUG;
                    if (i % frequency == 0) {
                        level = Level.WARN; // trigger buffer flush
                    }
                    bufferedContextualAppender.doAppend(createLoggingEvent(level));
                }
                totalCount.getAndAdd(amount);
            }
        };

        for (int i = 0; i < 100; i++) {
            executorService.execute(threadCode);
        }



        while (totalCount.get() < 100000) {
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
        }

        executorService.shutdown();

        stopWatch.stop();
        printStats(stopWatch, "100,000 multi threaded with random buffer flushes");
    }

    private void printStats(StopWatch stopWatch, String stat) {
        final long msTime = stopWatch.getTime();
        final long nsTime = stopWatch.getNanoTime();
        System.out.println(stat + " took " + nsTime + "ns (" + msTime + "ms)");
    }

    private static ILoggingEvent createLoggingEvent(Level level) {
        final LoggingEvent loggingEvent = new LoggingEvent();
        loggingEvent.setLevel(level);
        return loggingEvent;
    }

}
