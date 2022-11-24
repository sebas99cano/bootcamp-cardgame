package org.example.cardgame.application.command.adapter.bus;


import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.example.cardgame.application.command.GsonEventSerializer;
import org.example.cardgame.application.command.handle.BusinessLookUp;
import org.example.cardgame.generic.DomainEvent;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;


@Component
public class RabbitMQEventConsumer  {
    private final Tracer tracer;
    private final GsonEventSerializer serializer;
    private final BusinessLookUp businessLookUp;

    public RabbitMQEventConsumer(Tracer tracer,  GsonEventSerializer serializer,  BusinessLookUp businessLookUp) {
        this.tracer = tracer;
        this.serializer = serializer;
        this.businessLookUp = businessLookUp;
    }


    public void receiveMessage(String message) {
        var notification = Notification.from(message);
        var context = TraceContext.newBuilder()
                .parentId(notification.getParentId())
                .spanId(notification.getSpanId())
                .traceId(notification.getTraceId())
                .build();
        try {
            var event = serializer.deserialize(
                    notification.getBody(), Class.forName(notification.getType())
            );
            Span span = createSpan(notification, context, event);
            businessLookUp.get(event.type)
                    .doFirst(span::start)
                    .flatMap(businessService -> businessService.doProcessing(event))
                    .then(Mono.defer(() -> {
                        span.finish();
                        return Mono.just(message);
                    })).subscribe();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }

    private Span createSpan(Notification notification, TraceContext context, DomainEvent event) {
        return tracer.newChild(context)
                .name("consumer")
                .tag("eventType", event.type)
                .tag("aggregateRootId", event.aggregateRootId())
                .tag("aggregate", event.getAggregateName())
                .tag("uuid", event.uuid.toString())
                .annotate(notification.getBody())
                .kind(Span.Kind.SERVER);
    }
}
