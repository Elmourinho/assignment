package com.example.assignment.e2e;

import com.example.assignment.OutgoingMessageSender;
import java.util.Collections;
import javax.jms.JMSException;
import javax.jms.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestJmsConfig.class)
public class OutgoingMessageSenderIntegrationTest {

	@Autowired
	private OutgoingMessageSender outgoingMessageSender;

	@Autowired
	private OutgoingMessageListener outgoingMessageListener;

	@Autowired
	private JmsMessageListenerAspect jmsMessageListenerAspect;

	@Test(timeout = 30000)
	public void testIncomingMessageListener() throws JMSException {
		OutgoingMessageHandler outgoingMessageHandler = Mockito.mock(OutgoingMessageHandler.class);
		ReflectionTestUtils.setField(outgoingMessageListener, "outgoingMessageHandler", outgoingMessageHandler);

		// send message to a destination queue
		outgoingMessageSender.send(Collections.singletonMap("key", "value"));

		// wait for topic listener to handle an outgoing message
		jmsMessageListenerAspect.awaitCompletion();

		// verify outgoing message properties
		ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
		verify(outgoingMessageHandler).handle(messageCaptor.capture());

		Message message = messageCaptor.getValue();
		assertEquals("value", message.getStringProperty("key"));
	}

}