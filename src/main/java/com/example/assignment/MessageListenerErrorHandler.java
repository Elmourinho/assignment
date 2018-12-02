package com.example.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class MessageListenerErrorHandler implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageListenerErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        log.error("An unhanded message listener error occurred.", t);
    }

}
