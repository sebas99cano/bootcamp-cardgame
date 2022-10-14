package org.example.cardgame.application.command.adapter.bus;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.command.ApplicationConfig;
import org.example.cardgame.application.command.GsonEventSerializer;
import org.example.cardgame.application.command.generic.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Service
public class RabbitMQEventBus implements EventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQEventBus.class);

    private final Sender sender;
    private final GsonEventSerializer serializer;

    public RabbitMQEventBus(Sender sender,  GsonEventSerializer serializer) {
        this.serializer = serializer;
        this.sender = sender;
    }

    @Override
    public void publish(DomainEvent event) {
        sender.sendWithPublishConfirms(buildOutboundMessage(event))
                .doOnError(e -> LOGGER.error("Send failed", e))
                .subscribe(m -> {
                    if(m.isAck()) {
                        LOGGER.info("Message sent "+ event.type);
                    }
                });

    }

    @Override
    public void publishError(Throwable errorEvent) {
        var event = new ErrorEvent(errorEvent.getClass().getTypeName(), errorEvent.getMessage());
        sender.sendWithPublishConfirms(buildOutboundMessage(event))
                .doOnError(e -> LOGGER.error("Send failed", e))
                .subscribe(m -> {
                    if(m.isAck()) {
                        LOGGER.error("Message sent "+ event.type);
                    }
                });
    }

    private Mono<OutboundMessage> buildOutboundMessage(DomainEvent event) {
        var notification = new Notification(
                event.getClass().getTypeName(),
                serializer.serialize(event)
        );
        return  Mono.just(new OutboundMessage(
                ApplicationConfig.EXCHANGE, event.type, notification.serialize().getBytes()
        ));
    }


}