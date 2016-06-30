package com.github.sdegroot.logback.logbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class BufferedContextualAppenderTest {

    private BufferedContextualAppender bufferedContextualAppender;
    private BufferedAppenderWrapper stubAppender;

    @Before
    public void setUp() {
        stubAppender = mock(BufferedAppenderWrapper.class);
        bufferedContextualAppender = new BufferedContextualAppender(stubAppender, 5);
    }

    @After
    public void cleanBuffer() {
        verifyNoMoreInteractions(stubAppender);
        bufferedContextualAppender.cleanBuffer();
    }

    @Test
    public void shouldNotLogDirectlyButBuffer() {
        bufferedContextualAppender.setBufferFrom(Level.TRACE);
        bufferedContextualAppender.setBufferUntil(Level.WARN);
        bufferedContextualAppender.setFlushBufferFrom(Level.ERROR);

        final ILoggingEvent iLoggingEvent = logEvent(Level.DEBUG);
        bufferedContextualAppender.doAppend(iLoggingEvent);

        verify(stubAppender, times(0)).appendDirectly(iLoggingEvent);
    }

    @Test
    public void shouldLogDireclty() {
        bufferedContextualAppender.setBufferFrom(Level.TRACE);
        bufferedContextualAppender.setBufferUntil(Level.DEBUG);
        bufferedContextualAppender.setFlushBufferFrom(Level.ERROR);

        final ILoggingEvent iLoggingEvent = logEvent(Level.INFO);
        bufferedContextualAppender.doAppend(iLoggingEvent);

        verify(stubAppender, times(1)).appendDirectly(iLoggingEvent);
    }

    @Test
    public void shouldFlushBuffer() {
        bufferedContextualAppender.setBufferFrom(Level.TRACE);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.WARN);


        final ILoggingEvent iLoggingEventInfo = logEvent(Level.INFO);
        bufferedContextualAppender.doAppend(iLoggingEventInfo);

        verify(stubAppender, times(0)).appendDirectly(iLoggingEventInfo);

        final ILoggingEvent iLoggingEventWarn = logEvent(Level.WARN);
        bufferedContextualAppender.doAppend(iLoggingEventWarn);

        verify(stubAppender, times(1)).appendDirectly(iLoggingEventInfo);
        verify(stubAppender, times(1)).appendDirectly(iLoggingEventWarn);
    }

    @Test
    public void shouldLogLastItemsInBuffer() {
        bufferedContextualAppender.setBufferFrom(Level.DEBUG);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.ERROR);


        // log six times because buffer is 5 items large
        bufferedContextualAppender.doAppend(logEvent(Level.TRACE));
        bufferedContextualAppender.doAppend(logEvent(Level.INFO));
        bufferedContextualAppender.doAppend(logEvent(Level.INFO));
        bufferedContextualAppender.doAppend(logEvent(Level.INFO));
        bufferedContextualAppender.doAppend(logEvent(Level.INFO));
        bufferedContextualAppender.doAppend(logEvent(Level.INFO));
        bufferedContextualAppender.doAppend(logEvent(Level.INFO));

        // trigger buffer flush
        bufferedContextualAppender.doAppend(logEvent(Level.ERROR));

        verify(stubAppender, times(0)).appendDirectly(logEvent(Level.TRACE));
        verify(stubAppender, times(5)).appendDirectly(logEvent(Level.INFO));
        verify(stubAppender, times(1)).appendDirectly(logEvent(Level.ERROR));
    }

    @Test
    public void shouldLogEventsDirectlyIfBelowBufferFrom() {
        bufferedContextualAppender.setBufferFrom(Level.DEBUG);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.WARN);
        bufferedContextualAppender.setDropBelowBufferFrom(false);


        final ILoggingEvent iLoggingEventInfo = logEvent(Level.TRACE);
        bufferedContextualAppender.doAppend(iLoggingEventInfo);

        verify(stubAppender, times(1)).appendDirectly(iLoggingEventInfo);
    }

    @Test
    public void shouldNotLogMessagesAfterFlushing() {
        bufferedContextualAppender.setBufferFrom(Level.DEBUG);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.WARN);
        bufferedContextualAppender.setDropBelowBufferFrom(true);
        bufferedContextualAppender.setLogMessagesAfterFlush(false);


        bufferedContextualAppender.doAppend(logEvent(Level.DEBUG));
        bufferedContextualAppender.doAppend(logEvent(Level.ERROR));
        bufferedContextualAppender.doAppend(logEvent(Level.DEBUG));

        verify(stubAppender, times(1)).appendDirectly(logEvent(Level.DEBUG));
        verify(stubAppender, times(1)).appendDirectly(logEvent(Level.ERROR));
    }

    @Test
    public void shouldDirectlyLogMessagesFollowedByABufferFlush() {
        bufferedContextualAppender.setBufferFrom(Level.DEBUG);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.WARN);
        bufferedContextualAppender.setDropBelowBufferFrom(true);
        bufferedContextualAppender.setLogMessagesAfterFlush(true);


        bufferedContextualAppender.doAppend(logEvent(Level.DEBUG));
        bufferedContextualAppender.doAppend(logEvent(Level.ERROR));
        bufferedContextualAppender.doAppend(logEvent(Level.DEBUG));

        verify(stubAppender, times(2)).appendDirectly(logEvent(Level.DEBUG));
        verify(stubAppender, times(1)).appendDirectly(logEvent(Level.ERROR));
    }

    @Test
    public void shouldBufferAgainAfter5directlySendMessagse() {
        bufferedContextualAppender.setBufferFrom(Level.DEBUG);
        bufferedContextualAppender.setBufferUntil(Level.INFO);
        bufferedContextualAppender.setFlushBufferFrom(Level.WARN);
        bufferedContextualAppender.setDropBelowBufferFrom(true);
        bufferedContextualAppender.setLogMessagesAfterFlush(true);


        bufferedContextualAppender.doAppend(logEvent(Level.DEBUG));
        bufferedContextualAppender.doAppend(logEvent(Level.ERROR));

        for (int i = 0; i < 6; i++) {
            bufferedContextualAppender.doAppend(logEvent(Level.DEBUG));
        }

        verify(stubAppender, times(7)).appendDirectly(logEvent(Level.DEBUG));
        verify(stubAppender, times(1)).appendDirectly(logEvent(Level.ERROR));
    }

    public ILoggingEvent logEvent(Level level) {
        final LoggingEvent loggingEvent = new LoggingEvent() {
            @Override
            public boolean equals(Object obj) {
                return ((ILoggingEvent) obj).getLevel().equals(this.getLevel());
            }
        };
        loggingEvent.setLevel(level);
        return loggingEvent;
    }


}
