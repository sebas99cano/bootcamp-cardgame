package org.example.cardgame.application.command;


import com.mongodb.reactivestreams.client.MongoClient;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.cardgame.generic.EventPublisher;
import org.example.cardgame.generic.EventStoreRepository;
import org.example.cardgame.generic.IntegrationHandle;
import org.example.cardgame.generic.serialize.EventSerializer;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;



@Configuration
@ComponentScan(value="org.example.cardgame.usecase",
        useDefaultFilters = false, includeFilters = @ComponentScan.Filter
        (type = FilterType.REGEX, pattern = ".*UseCase")
)
public class ApplicationConfig {
    public static final String EXCHANGE = "core-game";
    public static final String QUEUE = "juego.commandhandles";
    public static final String STORE_NAME = "juego";

    private final  AmqpAdmin amqpAdmin;
    private final MongoClient mongoClient;

    public ApplicationConfig(AmqpAdmin amqpAdmin, MongoClient mongoClient) {
        this.amqpAdmin = amqpAdmin;
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    public void init() {
        var exchange = new TopicExchange(EXCHANGE);
        var queue = new Queue(QUEUE, false, false, true);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("cardgame.#"));
    }

    @Bean
    public Mono<Connection> connectionMono() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        return Mono.fromCallable(() -> connectionFactory.newConnection(STORE_NAME)).cache();
    }

    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono) {
        return new SenderOptions()
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }


    @Bean
    public ReceiverOptions receiverOptions(Mono<Connection> connectionMono) {
        return new ReceiverOptions()
                .connectionMono(connectionMono);
    }

    @Bean
    public Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, STORE_NAME+"db");
    }

    @Bean
    public IntegrationHandle integrationHandle(EventStoreRepository repository, EventPublisher eventPublisher, EventSerializer eventSerializer){
        return new IntegrationHandle(STORE_NAME, repository, eventPublisher, eventSerializer);
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

