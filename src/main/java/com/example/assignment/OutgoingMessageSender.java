package com.example.assignment;

import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class OutgoingMessageSender {

    private final JmsTemplate jmsTemplate;
    private final Topic destinationTopic;

    @Autowired
    public OutgoingMessageSender(JmsTemplate jmsTemplate, Topic destinationTopic) {
        this.jmsTemplate = jmsTemplate;
        this.destinationTopic = destinationTopic;
    }

    public void send(Map<String, String> properties) {
        jmsTemplate.convertAndSend(destinationTopic, "", message -> fillProperties(message, properties));
    }

    Message fillProperties(Message message, Map<String, String> properties) {
        properties.forEach((property, value) -> {
            try {
                message.setStringProperty(property, value);
            } catch (JMSException e) {
                throw new IllegalStateException("Failed to set message property.", e);
            }
        });
        return message;
    }

}
