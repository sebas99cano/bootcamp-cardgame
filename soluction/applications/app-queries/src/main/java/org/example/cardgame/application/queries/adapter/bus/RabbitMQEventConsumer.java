package org.example.cardgame.application.queries.adapter.bus;


import org.example.cardgame.application.queries.ConfigProperties;
import org.example.cardgame.application.queries.GsonEventSerializer;
import org.example.cardgame.application.queries.handle.materialize.MaterializeLookUp;
import org.example.cardgame.generic.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.ConsumeOptions;
import reactor.rabbitmq.ExceptionHandlers;
import reactor.rabbitmq.Receiver;

import java.time.Duration;



@Component
public class RabbitMQEventConsumer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQEventConsumer.class);

    private final GsonEventSerializer serializer;
    private final ConfigProperties configProperties;
    private final Receiver receiver;
    private final MaterializeLookUp materializeLookUp;

    public RabbitMQEventConsumer(GsonEventSerializer serializer, ConfigProperties configProperties, Receiver receiver, MaterializeLookUp materializeLookUp) {
        this.serializer = serializer;
        this.configProperties = configProperties;
        this.receiver = receiver;
        this.materializeLookUp = materializeLookUp;
    }


    @Override
    public void run(String... args) {
        receiver.consumeManualAck(configProperties.getQueue(), new ConsumeOptions()
                .exceptionHandler(new ExceptionHandlers.RetryAcknowledgmentExceptionHandler(
                        Duration.ofSeconds(20), Duration.ofMillis(500),
                        ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
                )))
                .subscribe(message -> {
                    var notification = Notification.from(new String(message.getBody()));
                    try {
                        DomainEvent event = serializer.deserialize(
                                notification.getBody(), Class.forName(notification.getType())
                        );
                        LOGGER.info("Receiver => "+event.type);
                        materializeLookUp.get(event.type)
                                .flatMap(materializeService -> materializeService.doProcessing(event))
                                .subscribe(unused -> {
                                    message.ack();
                                });
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
    }
}
