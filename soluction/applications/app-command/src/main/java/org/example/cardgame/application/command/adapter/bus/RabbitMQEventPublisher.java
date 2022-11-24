package org.example.cardgame.application.command.adapter.bus;

import brave.Span;
import brave.Tracer;
import org.example.cardgame.application.command.ConfigProperties;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.generic.ErrorEvent;
import org.example.cardgame.generic.EventPublisher;
import org.example.cardgame.generic.serialize.EventSerializer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQEventPublisher implements EventPublisher {
    private final Tracer tracer;

    private final RabbitTemplate rabbitTemplate;
    private final EventSerializer eventSerializer;
    private final ConfigProperties configProperties;

    public RabbitMQEventPublisher(Tracer tracer, RabbitTemplate rabbitTemplate, EventSerializer eventSerializer, ConfigProperties configProperties) {
        this.tracer = tracer;
        this.rabbitTemplate = rabbitTemplate;
        this.eventSerializer = eventSerializer;
        this.configProperties = configProperties;
    }

    @Override
    public void publish(DomainEvent event) {
        var eventBody = eventSerializer.serialize(event);

        Span span = createSpan(event, eventBody, null).start();
        var notification = new Notification(
                event.getClass().getTypeName(),
                eventBody,
                span.context().traceId(),
                span.context().parentId(),
                span.context().spanId(),
                span.context().extra()
        );

        rabbitTemplate.convertAndSend(configProperties.getExchange(), event.type, notification.serialize());

        span.finish();

    }

    private Span createSpan(DomainEvent event, String eventBody, Throwable error) {
        return tracer.nextSpan()
                .name("publisher")
                .tag("eventType", event.type)
                .tag("aggregateRootId", event.aggregateRootId())
                .tag("aggregate", event.getAggregateName())
                .tag("uuid", event.uuid.toString())
                .annotate(eventBody)
                .error(error)
                .kind(Span.Kind.CLIENT);
    }

    @Override
    public void publishError(Throwable errorEvent) {
        var event = new ErrorEvent(errorEvent.getClass().getTypeName(), errorEvent.getMessage());
        var eventBody = eventSerializer.serialize(event);
        Span span = createSpan(event, eventBody, errorEvent).start();
        var notification = new Notification(
                event.getClass().getTypeName(),
                eventBody,
                span.context().traceId(),
                span.context().parentId(),
                span.context().spanId(),
                span.context().extra()
        );
        rabbitTemplate.convertAndSend(configProperties.getExchange(), event.type, notification.serialize());

        span.finish();
    }

}