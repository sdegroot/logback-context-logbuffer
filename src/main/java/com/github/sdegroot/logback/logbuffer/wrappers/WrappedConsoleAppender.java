package com.github.sdegroot.logback.logbuffer.wrappers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.github.sdegroot.logback.logbuffer.BufferedAppenderWrapper;
import com.github.sdegroot.logback.logbuffer.BufferedContextualAppender;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is a simple wrapper around the ConsoleAppender.
 */
public class WrappedConsoleAppender extends ConsoleAppender<ILoggingEvent> implements BufferedAppenderWrapper {
    private BufferedContextualAppender bufferedContextualAppender;

	/*
     * Do note that the properties here are preceeded with an underscore for a reason.
	 * If we name them directly then Joran (component of Logback) will not handle it correctly.
	 */

    @Getter
    private Level _bufferFrom;
    @Getter
    private Level _bufferUntil;
    @Getter
    private Level _flushBufferFrom;
    @Setter
    private Integer bufferSize = BufferedContextualAppender.DEFAULT_BUFFER_SIZE;

    private Boolean bufferEnabled = Boolean.TRUE;

    @Override
    public void doAppend(ILoggingEvent eventObject) {
        if (Boolean.TRUE.equals(bufferEnabled)) {
            bufferedContextualAppender.doAppend(eventObject);
        } else {
            appendDirectly(eventObject);
        }
    }

    @Override
    public void appendDirectly(ILoggingEvent event) {
        super.doAppend(event);
    }

    @Override
    public void start() {
        bufferedContextualAppender = new BufferedContextualAppender(this, bufferSize);
        bufferedContextualAppender.setBufferFrom(_bufferFrom);
        bufferedContextualAppender.setBufferUntil(_bufferUntil);
        bufferedContextualAppender.setFlushBufferFrom(_flushBufferFrom);
        super.start();
    }

    @Override
    public void stop() {
        bufferedContextualAppender.cleanBuffer();
        super.stop();
    }

    @Override
    public void setBufferFrom(String level) {
        _bufferFrom = Level.toLevel(level);
    }

    @Override
    public void setBufferUntil(String level) {
        _bufferUntil = Level.toLevel(level);
    }

    @Override
    public void setFlushBufferFrom(String level) {
        _flushBufferFrom = Level.toLevel(level);
    }

    public void setBufferEnabled(String enabled) {
        this.bufferEnabled = Boolean.parseBoolean(enabled);
    }

}