[![Build Status](https://travis-ci.org/sdegroot/logback-context-logbuffer.svg?branch=master)](https://travis-ci.org/sdegroot/logback-context-logbuffer)
[![Coverage Status](https://coveralls.io/repos/github/sdegroot/logback-context-logbuffer/badge.svg?branch=master)](https://coveralls.io/github/sdegroot/logback-context-logbuffer?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.sdegroot/logback-context-logbuffer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.sdegroot/logback-context-buffer)

# logback-context-logbuffer

Logback plugin that allows to buffer logs messages until the loglevel of a log messages surpasses the set threshold 

## The problem
Logging is crucial for any system. It serves many purposes but one of the main purposes of general application logging is to help understand cases where something went wrong. 

In the world of logging we use different levels of logging. Examples are *DEBUG, INFO, WARN and ERROR*. Each of them serves a purpose but in general the lower the level, the more information you will get. 

It is a common struggle for developers and operations to determine what the log level should be. One the one hand you want to have DEBUG information available for the problem cases. However, on the other side you don't want the system to log everything on DEBUG level as results into enormous amounts of logs.

This tool provides a solution for this problem. **Instead of of having to make the choice for a static log level, the *logback-context-logbuffer* will only print DEBUG logging in cases where something went wrong.**

## How it works
The *logback-context-logbuffer* works by creating an internal fifo-buffer of a configurable amount of log messages.

It will keep all log messages of all levels (as configured in logback.xml) in this buffer. As soon as it receives a log message of a certain level (typically *WARN/ERROR*) it will flush it's buffer to the appender.

## Supported appenders

0. ConsoleAppender
0. RollingFileAppender

Other appenders are not yet supported. Please feel free to create a pull request.

## Common pitfalls

- You have set the log level to DEBUG in logback

For a working example see the *examples* directory.

## TODO

2. Make a release to Maven Central

## References

- https://issues.sonatype.org/browse/OSSRH-23410
