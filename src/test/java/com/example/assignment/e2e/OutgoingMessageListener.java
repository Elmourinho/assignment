package com.example.assignment.e2e;

import javax.jms.JMSException;
import javax.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OutgoingMessageListener {

	private final OutgoingMessageHandler outgoingMessageHandler;

	@Autowired
	public OutgoingMessageListener(OutgoingMessageHandler outgoingMessageHandler) {
		this.outgoingMessageHandler = outgoingMessageHandler;
	}

	@JmsListener(destination = "${app.jms.destination.topic}", containerFactory = "destinationJmsListenerContainerFactory")
	void onMessage(Message message) throws JMSException {
		outgoingMessageHandler.handle(message);
	}

}
