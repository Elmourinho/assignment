package com.example.assignment.e2e;

import javax.jms.JMSException;
import javax.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutgoingMessageHandler {

	private static final Logger log = LoggerFactory.getLogger(OutgoingMessageHandler.class);

	void handle(Message message) throws JMSException {
		log.info("Handled outgoing message: {}", message.getJMSMessageID());
	}

}
