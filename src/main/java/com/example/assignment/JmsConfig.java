package com.example.assignment;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {

    @Value("${app.jms.source.url}")
    private String sourceBrokerUrl;

    @Value("${app.jms.source.username}")
    private String sourceBrokerUsername;

    @Value("${app.jms.source.password}")
    private String sourceBrokerPassword;

    @Value("${app.jms.destination.url}")
    private String destinationBrokerUrl;

    @Value("${app.jms.destination.username}")
    private String destinationBrokerUsername;

    @Value("${app.jms.destination.password}")
    private String destinationBrokerPassword;

    @Value("${app.jms.destination.topic}")
    private String destinationTopicName;

    @Bean
    public ConnectionFactory sourceJmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(sourceBrokerUrl);

        if (sourceBrokerUsername != null) {
            connectionFactory.setUserName(sourceBrokerUsername);
        }
        if (sourceBrokerPassword != null) {
            connectionFactory.setPassword(sourceBrokerPassword);
        }

        return connectionFactory;
    }

    @Bean
    public ConnectionFactory destinationJmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(destinationBrokerUrl);

        if (destinationBrokerUsername != null) {
            connectionFactory.setUserName(destinationBrokerUsername);
        }
        if (destinationBrokerPassword != null) {
            connectionFactory.setPassword(destinationBrokerPassword);
        }

        return connectionFactory;
    }

    @Bean
    public Topic destinationTopic() {
        return new ActiveMQTopic(destinationTopicName);
    }

    @Bean
    public JmsTemplate destinationQueueJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(destinationJmsConnectionFactory());
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> sourceJmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sourceJmsConnectionFactory());
        factory.setErrorHandler(messageListenerErrorHandler());
        factory.setPubSubDomain(false);

        return factory;
    }

    @Bean
    public MessageListenerErrorHandler messageListenerErrorHandler() {
        return new MessageListenerErrorHandler();
    }

}
