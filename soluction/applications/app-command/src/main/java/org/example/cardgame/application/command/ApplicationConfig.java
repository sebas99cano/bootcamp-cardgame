package org.example.cardgame.application.command;



import com.mongodb.reactivestreams.client.MongoClient;
import org.example.cardgame.application.command.adapter.bus.RabbitMQEventConsumer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.example.cardgame.generic.EventPublisher;
import org.example.cardgame.generic.EventStoreRepository;
import org.example.cardgame.generic.IntegrationHandle;
import org.example.cardgame.generic.serialize.EventSerializer;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;
import java.util.Arrays;



@Configuration
@ComponentScan(value="org.example.cardgame.usecase",
        useDefaultFilters = false, includeFilters = @ComponentScan.Filter
        (type = FilterType.REGEX, pattern = ".*UseCase")
)
public class ApplicationConfig {


    private final  AmqpAdmin amqpAdmin;
    private final MongoClient mongoClient;
    private final ConfigProperties configProperties;

    public ApplicationConfig(AmqpAdmin amqpAdmin, MongoClient mongoClient, ConfigProperties configProperties) {
        this.amqpAdmin = amqpAdmin;
        this.mongoClient = mongoClient;
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
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, "game-events");
    }

    @Bean
    public IntegrationHandle integrationHandle(EventStoreRepository repository, EventPublisher eventPublisher, EventSerializer eventSerializer){
        return new IntegrationHandle(configProperties.getStoreName(), repository, eventPublisher, eventSerializer);
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

