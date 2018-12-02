package com.example.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class IncomingMessageListener {

    private final IncomingMessageHandler incomingMessageHandler;

    @Autowired
    public IncomingMessageListener(IncomingMessageHandler incomingMessageHandler) {
        this.incomingMessageHandler = incomingMessageHandler;
    }

    @JmsListener(destination = "${app.jms.source.queue}", containerFactory = "sourceJmsListenerContainerFactory")
    void onMessage(String payload) {
        incomingMessageHandler.handle(payload);
    }

}
