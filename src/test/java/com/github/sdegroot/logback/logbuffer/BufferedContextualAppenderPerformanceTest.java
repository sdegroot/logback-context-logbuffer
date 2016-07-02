package com.github.sdegroot.logback.logbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * This is a "performance test" that doesn't assert anything. It just shows the differences a bit.
 */
public class BufferedContextualAppenderPerformanceTest {

    private BufferedContextualAppender bufferedContextualAppender;
    private BufferedAppenderWrapper stubAppender;

    @Before
    public void setUp() {
        stubAppender = mock(BufferedAppenderWrapper.class);
        bufferedContextualAppender = new BufferedContextualAppender(stubAppender, 50);
        bufferedContextualAppender.setBufferFrom(Level.DEBUG);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.WARN);
        bufferedContextualAppender.setLogMessagesAfterFlush(true);
        bufferedContextualAppender.setDropBelowBufferFrom(true);
    }

    @Test
    public void Calls1000WithoutBufferFlush() {
        final StopWatch stopWatch = new StopWatch();
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
        final StopWatch stopWatch = new StopWatch();
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
        final StopWatch stopWatch = new StopWatch();
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
