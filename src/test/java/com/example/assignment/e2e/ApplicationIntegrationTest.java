package com.example.assignment.e2e;

import com.example.assignment.utils.FileUtils;
import javax.jms.JMSException;
import javax.jms.Message;
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
public class ApplicationIntegrationTest {

    @Autowired
    private JmsTemplate queueJmsTemplate;

    @Autowired
    private JmsMessageListenerAspect jmsMessageListenerAspect;

    @Autowired
    private OutgoingMessageListener outgoingMessageListener;

    @Autowired
    private Queue sourceQueue;

    @Test(timeout = 30000)
    @SuppressWarnings("unchecked")
    public void testIncomingMessageListener() throws JMSException {
        OutgoingMessageHandler outgoingMessageHandler = Mockito.mock(OutgoingMessageHandler.class);
        ReflectionTestUtils.setField(outgoingMessageListener, "outgoingMessageHandler", outgoingMessageHandler);

        // send message to a source queue
        String payload = FileUtils.readResourceFileAsString("classpath:message-payload-valid.xml");
        queueJmsTemplate.convertAndSend(sourceQueue, payload);

        // wait for queue listener to handle incoming message
        jmsMessageListenerAspect.awaitCompletion();

        // wait for topic listener to handle outgoing message
        jmsMessageListenerAspect.awaitCompletion();

        // verify outgoing message properties
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(outgoingMessageHandler).handle(messageCaptor.capture());

        Message message = messageCaptor.getValue();
        assertEquals("bc2a55e8-5a07-4af6-85fd-8290d3ccfb51", message.getStringProperty("REQUEST_ID"));
        assertEquals("0de01919-81eb-4cc7-a51d-15f6085fc1a4", message.getStringProperty("UUID"));
        assertEquals("CLIE01", message.getStringProperty("CLIENT_ID"));
        assertEquals("20180100", message.getStringProperty("TRNVER"));
    }

}
