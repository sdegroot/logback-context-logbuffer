package com.github.sdegroot.logback.logbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class BufferedContextualAppenderIntegrationTest {

    private static Logger log;

    @BeforeClass
    public static void init() {
        log = (Logger) LoggerFactory.getLogger(BufferedContextualAppenderIntegrationTest.class);
    }

    private StubAppender stubAppender;

    @Before
    public void setUp() {
        stubAppender = new StubAppender();
        stubAppender.setName("StubAppender");
        log.addAppender(stubAppender);
        stubAppender.start();
    }

    @After
    public void tearDown() {
        log.detachAppender(stubAppender);
    }

    @Test
    public void shouldNotBufferIfLevelLowerThanFrom() {
        stubAppender.bufferedContextualAppender.setBufferFrom(Level.INFO);
        stubAppender.bufferedContextualAppender.setBufferUntil(Level.WARN);
        stubAppender.bufferedContextualAppender.setFlushBufferFrom(Level.ERROR);


        log.debug("Test");
        stubAppender.bufferedContextualAppender.flushBuffer();
        assertThat(stubAppender.loggedEvents, is(empty()));
    }

    @Test
    public void shouldBufferIfLevelShouldBeBuffered() {
        stubAppender.bufferedContextualAppender.setBufferFrom(Level.INFO);
        stubAppender.bufferedContextualAppender.setBufferUntil(Level.WARN);
        stubAppender.bufferedContextualAppender.setFlushBufferFrom(Level.ERROR);


        log.info("Test");
        stubAppender.bufferedContextualAppender.flushBuffer();
        assertThat(stubAppender.loggedEvents, hasSize(1));
    }

    @Test
    public void shouldFlushBufferIfTriggered() {
        stubAppender.bufferedContextualAppender.setBufferFrom(Level.INFO);
        stubAppender.bufferedContextualAppender.setBufferUntil(Level.WARN);
        stubAppender.bufferedContextualAppender.setFlushBufferFrom(Level.ERROR);


        log.info("Info");
        log.error("Error");
        assertThat(stubAppender.loggedEvents, hasSize(2));
    }


    public class StubAppender extends UnsynchronizedAppenderBase<ILoggingEvent> implements BufferedAppenderWrapper {
        public BufferedContextualAppender bufferedContextualAppender = new BufferedContextualAppender(this);

        public List<ILoggingEvent> loggedEvents = new ArrayList<>();

        @Override
        public void doAppend(ILoggingEvent eventObject) {
            bufferedContextualAppender.doAppend(eventObject);
        }

        @Override
        protected void append(ILoggingEvent iLoggingEvent) {
            loggedEvents.add(iLoggingEvent);
        }

        @Override
        public void appendDirectly(ILoggingEvent event) {
            loggedEvents.add(event);
        }

        @Override
        public void stop() {
            bufferedContextualAppender.cleanBuffer();
            super.stop();
        }

        @Override
        public void setBufferFrom(String level) {
        }

        @Override
        public void setBufferUntil(String level) {

        }

        @Override
        public void setFlushBufferFrom(String level) {

        }

        @Override
        public void setBufferSize(Integer size) {

        }
    }

}
