package com.github.sdegroot.logback.logbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LogbackException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 * This class contains the actual logic for buffering ILoggingEvents for certain appenders.
 * Use this class in conjunction with @{link BufferedAppenderWrapper}
 * <p>
 * Please make sure that you never log something in this class using regular loggers!
 * This will cause a stackoverflow since it will most likely be a recursive method.
 * <p>
 * <p>
 * TODO: improve performance around thread-safety
 *
 * @see @{link WrappedConsoleAppender} for an example on how to wrap another appender.
 */
public class BufferedContextualAppender {
    public static final int DEFAULT_BUFFER_SIZE = 50;

    @Getter
    private Level bufferFrom = Level.DEBUG;

    @Getter
    private Level bufferUntil = Level.INFO;

    @Getter
    private Level flushBufferFrom = Level.WARN;

    /**
     * LogEvents between bufferFrom and bufferUntil and up will be flushed to the actual appender.
     * Normally, logevents below bufferFrom are dropped.
     * Set this value to false if you do not want to drop these items.
     */
    @Getter
    @Setter
    private boolean dropBelowBufferFrom = true;

    @Getter
    private int bufferSize;

    /**
     * Enable this switch if you want log the messages that are sent directly after a buffer flush.
     * The logic behind this is that usually it's not only useful to know the log messages preceeding an error but also the ones directly after it.
     */
    @Getter
    @Setter
    private boolean logMessageAfterFlush = false;

    /**
     * This variable holds the count of messages that still need to be flushed directly.
     * If a message is logged and this number is higher than zero, then this message will not be buffered but logged directly instead.
     * The logic behind this is that usually it's not only useful to know the log messages preceeding an error but also the ones directly after it.
     */
    private int messagesToFlushDirectly = 0;

    private final CircularFifoQueue<ILoggingEvent> buffer;

    private final BufferedAppenderWrapper actualAppender;

    /**
     * Create a new appender with an internal buffer.
     * Will use the default buffer size.
     *
     * @param actualAppender the reference to the actual (concrete) appender. Will be used when the log event must be written.
     */
    public BufferedContextualAppender(BufferedAppenderWrapper actualAppender) {
        this(actualAppender, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Create a new appender with an internal buffer.
     *
     * @param actualAppender the reference to the actual (concrete) appender. Will be used when the log event must be written.
     * @param bufferSize     the amount of messages that this buffer will hold
     */
    public BufferedContextualAppender(BufferedAppenderWrapper actualAppender, int bufferSize) {
        this.actualAppender = actualAppender;
        this.bufferSize = bufferSize;

        buffer = new CircularFifoQueue<ILoggingEvent>(bufferSize);
    }

    /**
     * This method must be called from your wrapper in the doAppend method.
     * It will buffer your event or write it directly depending on the configuration.
     *
     * @throws LogbackException rethrows exceptions thrown by the actual wrapper
     */
    public void doAppend(ILoggingEvent event) throws LogbackException {
        final Level level = event.getLevel();

        synchronized (buffer) {
            if (messagesToFlushDirectly > 0) {
                messagesToFlushDirectly--;
            } else {

                // if the level of the event is between
                if (level.isGreaterOrEqual(bufferFrom) && bufferUntil.isGreaterOrEqual(level)) {
                    buffer.add(event);
                    // don't log it
                    return;
                }

                if (level.isGreaterOrEqual(flushBufferFrom)) {
                    // flush buffer
                    flushBuffer();
                } else if (dropBelowBufferFrom && bufferFrom.isGreaterOrEqual(level)) {
                    // drop this event because its below bufferFrom
                    return;
                }
            }
        }

        this.actualAppender.appendDirectly(event);
    }

    /**
     * This method is called by the appenders when they're stopped.
     * When this method is called we must assume that our actualAppender is going to stop, and therefore, so must we.
     * <p>
     * For now it will only flush the buffer.
     */
    public void cleanBuffer() {
        buffer.clear();
    }

    /**
     * Clears the buffer and appends the log messages directly to the actual appender.
     */
    void flushBuffer() {
        Object[] copy = buffer.toArray();
        cleanBuffer();

        for (Object e : copy) {
            this.actualAppender.appendDirectly((ILoggingEvent) e);
        }

        if (logMessageAfterFlush) {
            // ensure that the next DEFAULT_BUFFER_SIZE messages are logged directly without buffering.
            messagesToFlushDirectly = DEFAULT_BUFFER_SIZE;
        }
    }

    public void setBufferFrom(Level bufferFrom) {
        if (bufferFrom != null) {
            this.bufferFrom = bufferFrom;
        }
    }

    public void setBufferUntil(Level bufferUntil) {
        if (bufferUntil != null) {
            this.bufferUntil = bufferUntil;
        }
    }

    public void setFlushBufferFrom(Level flushBufferFrom) {
        if (flushBufferFrom != null) {
            this.flushBufferFrom = flushBufferFrom;
        }
    }
}
