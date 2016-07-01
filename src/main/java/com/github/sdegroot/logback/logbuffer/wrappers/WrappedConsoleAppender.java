package com.github.sdegroot.logback.logbuffer.wrappers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.github.sdegroot.logback.logbuffer.BufferedAppenderWrapper;
import com.github.sdegroot.logback.logbuffer.BufferedContextualAppender;
import lombok.Getter;

/**
 * This class is a simple wrapper around the ConsoleAppender.
 */
public class WrappedConsoleAppender extends ConsoleAppender<ILoggingEvent> implements BufferedAppenderWrapper {
    @Getter
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

    @Getter
    private Integer _bufferSize = BufferedContextualAppender.DEFAULT_BUFFER_SIZE;

    @Getter
    private Boolean _bufferEnabled = Boolean.TRUE;

    @Getter
    private Boolean _logMessagesAfterFlush = Boolean.TRUE;

    @Override
    public void doAppend(ILoggingEvent eventObject) {
        if (Boolean.TRUE.equals(_bufferEnabled)) {
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
        bufferedContextualAppender = new BufferedContextualAppender(this, _bufferSize);
        bufferedContextualAppender.setBufferFrom(_bufferFrom);
        bufferedContextualAppender.setBufferUntil(_bufferUntil);
        bufferedContextualAppender.setFlushBufferFrom(_flushBufferFrom);
        bufferedContextualAppender.setLogMessagesAfterFlush(_logMessagesAfterFlush);
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

    @Override
    public void setBufferSize(String size) {
        _bufferSize = Integer.parseInt(size);
    }

    @Override
    public void setBufferEnabled(String enabled) {
        this._bufferEnabled = Boolean.parseBoolean(enabled);
    }

    @Override
    public void setLogMessagesAfterFlush(String enabled) {
        this._logMessagesAfterFlush = Boolean.parseBoolean(enabled);
    }

}