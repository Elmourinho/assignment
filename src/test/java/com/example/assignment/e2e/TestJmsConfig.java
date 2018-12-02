package com.example.assignment.e2e;

import com.example.assignment.IncomingMessageHandler;
import com.example.assignment.IncomingMessageListener;
import com.example.assignment.OutgoingMessageSender;
import com.example.assignment.e2e.JmsMessageListenerAspect;
import com.example.assignment.e2e.OutgoingMessageHandler;
import com.example.assignment.e2e.OutgoingMessageListener;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@EnableJms
@EnableAspectJAutoProxy
@EnableAutoConfiguration(exclude = {JmsAutoConfiguration.class})
public class TestJmsConfig {

	@Value("${app.jms.source.url}")
	private String sourceBrokerUrl;

	@Value("${app.jms.source.queue}")
	private String sourceQueueName;

	@Value("${app.jms.destination.url}")
	private String destinationBrokerUrl;

	@Value("${app.jms.destination.topic}")
	private String destinationTopicName;

	private final ConnectionFactory connectionFactory;

	@Autowired
	public TestJmsConfig(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Bean
	public BrokerService brokerService() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.addConnector(sourceBrokerUrl);
		broker.setPersistent(false);
		broker.setCacheTempDestinations(false);
		broker.setDestinations(new ActiveMQDestination[]{destinationTopic()});
		return broker;
	}

	@Bean
	public ActiveMQQueue sourceQueue() {
		return new ActiveMQQueue(sourceQueueName);
	}

	@Bean
	public ActiveMQTopic destinationTopic() {
		return new ActiveMQTopic(destinationTopicName);
	}

	@Bean
	public JmsTemplate queueJmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		jmsTemplate.setPubSubDomain(true);

		return jmsTemplate;
	}

	@Bean
	public JmsTemplate topicJmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		jmsTemplate.setPubSubDomain(true);

		return jmsTemplate;
	}

	@Bean
	public JmsListenerContainerFactory<?> sourceJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(false);

		return factory;
	}

	@Bean
	public JmsListenerContainerFactory<?> destinationJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(true);

		return factory;
	}

	@Bean
	public OutgoingMessageSender messageSender() {
		return new OutgoingMessageSender(topicJmsTemplate(), destinationTopic());
	}

	@Bean
	public IncomingMessageListener messageListener() {
		return new IncomingMessageListener(messageHandler());
	}

	@Bean
	public IncomingMessageHandler messageHandler() {
		return new IncomingMessageHandler(messageSender());
	}

	@Bean
	public OutgoingMessageHandler messageTopicHandler() {
		return new OutgoingMessageHandler();
	}

	@Bean
	public OutgoingMessageListener messageTopicListener() {
		return new OutgoingMessageListener(messageTopicHandler());
	}

	@Bean
	public JmsMessageListenerAspect messageListenerAspect() {
		return new JmsMessageListenerAspect();
	}

}
