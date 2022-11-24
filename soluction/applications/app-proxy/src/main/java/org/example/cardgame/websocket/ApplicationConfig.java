package org.example.cardgame.websocket;


import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.PostConstruct;


@Configuration
public class ApplicationConfig {
    private final ConfigProperties configProperties;
    private final AmqpAdmin amqpAdmin;

    public ApplicationConfig(AmqpAdmin amqpAdmin,ConfigProperties configProperties) {
        this.amqpAdmin = amqpAdmin;
        this.configProperties = configProperties;
    }

    @PostConstruct
    public void init() {
        var exchange = new TopicExchange(configProperties.getExchange());
        var queue = new Queue(configProperties.getQueue(), false, false, true);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(configProperties.getRoutingKey()));
    }


    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(configProperties.getQueue());
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RabbitMQEventConsumer receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

