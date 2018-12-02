package com.example.assignment;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.jms.Message;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class OutgoingMessageSenderTest {

    @Spy
    @InjectMocks
    private OutgoingMessageSender outgoingMessageSender;

    @Mock
    private JmsTemplate jmsTemplate;

    @Test
    public void fillProperties_ShouldSetMessageProperties_GivenMessageAndProperties() throws JMSException {
        Message message = new ActiveMQTextMessage();
        message = outgoingMessageSender.fillProperties(message, generateProperties());

        for (String property : IncomingMessageHandler.XML_OBJECT_PROPERTIES) {
            Assertions.assertThat(property).isEqualTo(message.getStringProperty(property));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void fillProperties_ShouldThrowException_IfNotAbleToSetMessageProperties() throws JMSException {
        Message message = Mockito.mock(ActiveMQTextMessage.class);

        Mockito.doThrow(JMSException.class).when(message).setStringProperty(anyString(), anyString());

        outgoingMessageSender.fillProperties(message, generateProperties());
    }

    private Map<String, String> generateProperties() {
        return IncomingMessageHandler.XML_OBJECT_PROPERTIES.stream()
                .collect(Collectors.toMap(Function.identity(), Function.identity()));
    }

}
