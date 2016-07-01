package com.github.sdegroot.logback.logbuffer;

import ch.qos.logback.classic.Level;
import com.github.sdegroot.logback.logbuffer.wrappers.WrappedConsoleAppender;
import com.github.sdegroot.logback.logbuffer.wrappers.WrappedRollingFileAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * This test case tests all wrapper classes.
 * It is in one test because the wrappers are mostly the same anyway.
 */
@RunWith(Parameterized.class)
public class WrappersTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {WrappedConsoleAppender.class},
                {WrappedRollingFileAppender.class}
        });
    }

    final BufferedAppenderWrapper bufferedAppenderWrapper;

    public WrappersTest(Class<? extends BufferedAppenderWrapper> wrapperClass) throws IllegalAccessException, InstantiationException {
        bufferedAppenderWrapper = wrapperClass.newInstance();
    }

    @Test
    public void test() {
        bufferedAppenderWrapper.setBufferEnabled("false");
        bufferedAppenderWrapper.setBufferUntil("WARN");
        bufferedAppenderWrapper.setBufferSize("2");
        bufferedAppenderWrapper.setBufferFrom("INFO");
        bufferedAppenderWrapper.setLogMessagesAfterFlush("true");

        bufferedAppenderWrapper.start();

        final BufferedContextualAppender bufferedContextualAppender =
                bufferedAppenderWrapper.getBufferedContextualAppender();
        assertThat(bufferedContextualAppender.getBufferUntil(), is(equalTo(Level.WARN)));
        assertThat(bufferedContextualAppender.getBufferFrom(), is(equalTo(Level.INFO)));
        assertThat(bufferedContextualAppender.getBufferSize(), is(equalTo(2)));
    }


}
