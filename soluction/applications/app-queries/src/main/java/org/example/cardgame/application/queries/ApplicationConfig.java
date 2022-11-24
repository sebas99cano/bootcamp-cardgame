package org.example.cardgame.application.queries;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.cardgame.application.queries.adapter.bus.RabbitMQEventConsumer;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Arrays;


@Configuration
public class ApplicationConfig {


    private final AmqpAdmin amqpAdmin;
    private final ConfigProperties configProperties;

    public ApplicationConfig(AmqpAdmin amqpAdmin, ConfigProperties configProperties) {
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
    public SimpleMessageListenerContainer container(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
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
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}

