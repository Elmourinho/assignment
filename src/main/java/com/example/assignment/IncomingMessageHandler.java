package com.example.assignment;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.jxpath.JXPathContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class IncomingMessageHandler {

    static final String XML_OBJECT_BASE_SELECTOR = "/UC_STOCK_LEVEL_IFD/CTRL_SEG/";
    static final List<String> XML_OBJECT_PROPERTIES = Arrays.asList("UUID", "TRNVER", "CLIENT_ID", "REQUEST_ID");

    private final OutgoingMessageSender outgoingMessageSender;

    @Autowired
    public IncomingMessageHandler(OutgoingMessageSender outgoingMessageSender) {
        this.outgoingMessageSender = outgoingMessageSender;
    }

    public void handle(String data) {
        outgoingMessageSender.send(extractProperties(data));
    }

    Map<String, String> extractProperties(String data) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(data));
            Document doc = db.parse(is);

            JXPathContext context = JXPathContext.newContext(doc);

            Map<String, String> properties = new HashMap<>();
            for (String property : XML_OBJECT_PROPERTIES) {
                properties.put(property, (String) context.getValue(XML_OBJECT_BASE_SELECTOR + property));
            }

            return properties;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new IllegalStateException("Failed to parse message.", e);
        }
    }

}
