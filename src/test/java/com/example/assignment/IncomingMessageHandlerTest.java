package com.example.assignment;

import com.example.assignment.utils.FileUtils;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class IncomingMessageHandlerTest {

    @Spy
    @InjectMocks
    private IncomingMessageHandler incomingMessageHandler;

    @Mock
    private OutgoingMessageSender outgoingMessageSender;

    @Test
    public void extractProperties_ShouldReturnProperties_GivenValidXmlPayload() throws Exception {
        String payload = FileUtils.readResourceFileAsString("classpath:message-payload-valid.xml");
        Map<String, String> properties = incomingMessageHandler.extractProperties(payload);

        assertThat(properties.get("UUID")).isEqualTo("0de01919-81eb-4cc7-a51d-15f6085fc1a4");
        assertThat(properties.get("REQUEST_ID")).isEqualTo("bc2a55e8-5a07-4af6-85fd-8290d3ccfb51");
    }

    @Test(expected = RuntimeException.class)
    public void extractProperties_ShouldThrowException_GivenInvalidXmlPayload() {
        String payload = FileUtils.readResourceFileAsString("classpath:message-payload-invalid.xml");
        incomingMessageHandler.extractProperties(payload);
    }

    @Test(expected = RuntimeException.class)
    public void extractProperties_ShouldThrowException_GivenXmlPayloadWithInvalidStructure() {
        String payload = FileUtils.readResourceFileAsString("classpath:message-payload-invalid-structure.xml");
        incomingMessageHandler.extractProperties(payload);
    }

}
