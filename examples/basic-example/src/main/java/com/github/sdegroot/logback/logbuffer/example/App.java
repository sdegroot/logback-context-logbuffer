package com.github.sdegroot.logback.logbuffer.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger("chapters.introduction.HelloWorld1");
        logger.debug("This should be logged");
        logger.warn("This triggers the buffer to be cleared");
        logger.debug("This should be logged (after the warning; 1)");
        logger.debug("This should be logged (after the warning; 2)");
        logger.debug("This should be not logged");

    }
}
