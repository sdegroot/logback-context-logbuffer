package com.github.sdegroot.logback.logbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * This interface needs to be implemented by the appender wrappers.
 * The method of this appender 'appendDirectly' basically talks directly to the underlying appender.
 * <p>
 * Use this class in conjunction with @{link BufferedContextualAppender}
 *
 * @see @{link WrappedConsoleAppender} for an example on how to wrap another appender.
 */
public interface BufferedAppenderWrapper {
    void appendDirectly(ILoggingEvent event);

    /**
     * The stop method is called by Joran from Logback.
     * This method must be call @{link BufferedContextualAppender.clearBuffer} to avoid memory leaks.
     * <p>
     * Note that this method doesn't necessarily have to be in this interface, but it makes it very explicit.
     */
    void stop();

    /**
     * The start method is called by Joran from Logback.
     * <p>
     * Note that this method doesn't necessarily have to be in this interface, but it makes it very explicit.
     */
    void start();

    void setBufferFrom(String level);

    void setBufferUntil(String level);

    void setFlushBufferFrom(String level);

    void setBufferSize(String size);

    void setLogMessagesAfterFlush(String enabled);

    void setBufferEnabled(String enabled);
}
