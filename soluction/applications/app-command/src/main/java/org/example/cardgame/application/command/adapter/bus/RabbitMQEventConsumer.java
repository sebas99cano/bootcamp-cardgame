package org.example.cardgame.application.command.adapter.bus;


import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.command.GsonEventSerializer;
import org.example.cardgame.application.command.handle.usecase.BusinessLookUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.ConsumeOptions;
import reactor.rabbitmq.ExceptionHandlers;
import reactor.rabbitmq.Receiver;

import java.time.Duration;
import java.util.stream.Collectors;

import static org.example.cardgame.application.command.ApplicationConfig.QUEUE;


@Component
public class RabbitMQEventConsumer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQEventConsumer.class);

    private final GsonEventSerializer serializer;
    private final Receiver receiver;
    private final BusinessLookUp businessLookUp;

    public RabbitMQEventConsumer(GsonEventSerializer serializer, Receiver receiver, BusinessLookUp businessLookUp){
        this.serializer = serializer;
        this.receiver = receiver;
        this.businessLookUp = businessLookUp;
    }


    @Override
    public void run(String... args) {
        receiver.consumeManualAck(QUEUE, new ConsumeOptions()
                .exceptionHandler(new ExceptionHandlers.RetryAcknowledgmentExceptionHandler(
                        Duration.ofSeconds(20), Duration.ofMillis(500),
                        ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
                )))
                .flatMap(message -> {
                    var notification = Notification.from(new String(message.getBody()));
                    try {
                        DomainEvent event = serializer.deserialize(
                                notification.getBody(), Class.forName(notification.getType())
                        );
                        return businessLookUp.get(event.type)
                                .flatMap(service -> service.doProcessing(event))
                                .map(e -> message);
                    } catch (ClassNotFoundException e) {
                        return Flux.error(new IllegalArgumentException(e));
                    }
                }).subscribe(message -> {
                    LOGGER.info(new String(message.getBody()));
                    message.ack();
                });
    }
}
