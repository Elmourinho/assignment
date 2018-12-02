package com.example.assignment.e2e;

import com.example.assignment.IncomingMessageHandler;
import com.example.assignment.IncomingMessageListener;
import com.example.assignment.utils.FileUtils;
import javax.jms.Queue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestJmsConfig.class)
public class IncomingMessageListenerIntegrationTest {

	@Autowired
	private JmsTemplate queueJmsTemplate;

	@Autowired
	private JmsMessageListenerAspect jmsMessageListenerAspect;

	@Autowired
	private IncomingMessageListener incomingMessageListener;

	@Autowired
	private Queue sourceQueue;

	@Test(timeout = 30000)
	public void testIncomingMessageListener() {
		IncomingMessageHandler incomingMessageHandler = Mockito.mock(IncomingMessageHandler.class);
		ReflectionTestUtils.setField(incomingMessageListener, "incomingMessageHandler", incomingMessageHandler);

		// send message to a source queue
		String payload = FileUtils.readResourceFileAsString("classpath:message-payload-valid.xml");
		queueJmsTemplate.convertAndSend(sourceQueue, payload);

		// wait for queue listener to handle incoming message
		jmsMessageListenerAspect.awaitCompletion();

		// verify outgoing message properties
		ArgumentCaptor<String> messagePayloadCaptor = ArgumentCaptor.forClass(String.class);
		verify(incomingMessageHandler).handle(messagePayloadCaptor.capture());

		assertEquals(payload, messagePayloadCaptor.getValue());
	}

}
